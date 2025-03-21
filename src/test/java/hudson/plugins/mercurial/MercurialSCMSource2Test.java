package hudson.plugins.mercurial;
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.domains.Domain;
import hudson.FilePath;
import hudson.model.Slave;
import hudson.plugins.mercurial.traits.MercurialInstallationSCMSourceTrait;
import hudson.util.StreamTaskListener;
import java.util.Collections;
import jenkins.branch.BranchSource;
import jenkins.scm.api.trait.SCMSourceTrait;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.jenkinsci.test.acceptance.docker.DockerClassRule;
import org.junit.ClassRule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.FlagRule;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;

public class MercurialSCMSource2Test {
  @ClassRule public static TestRule noSpaceInTmpDirs = FlagRule.systemProperty("jenkins.test.noSpaceInTmpDirs", "true");

  @ClassRule public static BuildWatcher buildWatcher = new BuildWatcher();

  @Rule public JenkinsRule r = new JenkinsRule();

  @Rule public MercurialRule m = new MercurialRule(r);

  @ClassRule public static DockerClassRule<MercurialContainer> docker = new DockerClassRule<>(MercurialContainer.class);

  @Rule public TemporaryFolder tmp = new TemporaryFolder();

  @Issue(value = { "JENKINS-42278", "JENKINS-46851", "JENKINS-48867" }) @Test public void withCredentialsId() throws Exception {
    m.hg("version");
    MercurialContainer container = docker.create();
    Slave slave = container.createSlave(r);
    m.withNode(slave);
    MercurialInstallation inst = container.createInstallation(r, MercurialContainer.Version.HG5, false, false, false, "", slave);
    assertNotNull(inst);
    m.withInstallation(inst);
    FilePath sampleRepo = slave.getRootPath().child("sampleRepo");
    sampleRepo.mkdirs();
    m.hg(sampleRepo, "init");
    sampleRepo.child("Jenkinsfile").write("node(\'master\') {checkout scm}", null);
    m.hg(sampleRepo, "commit", "--addremove", "--message=flow");
    MercurialSCMSource s = new MercurialSCMSource("ssh://test@" + container.ipBound(22) + ":" + container.port(22) + "/" + sampleRepo);
    CredentialsProvider.lookupStores(r.jenkins).iterator().next().addCredentials(Domain.global(), new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, "creds", "test", new BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource(container.getPrivateKey().getAbsolutePath()), null, null));
    s.setCredentialsId("creds");
    String toolHome = inst.forNode(slave, StreamTaskListener.fromStdout()).getHome();
    assertNotNull(toolHome);
    String remoteHgLoc = inst.executableWithSubstitution(toolHome);
    r.jenkins.getDescriptorByType(MercurialInstallation.DescriptorImpl.class).setInstallations(new MercurialInstallation("default", "", "hg", false, true, null, false, "[ui]\nssh = ssh -o UserKnownHostsFile=" + tmp.newFile("known_hosts") + " -o StrictHostKeyChecking=no\n" + "remotecmd = " + remoteHgLoc, null));
    s.setTraits(Collections.<SCMSourceTrait>singletonList(new MercurialInstallationSCMSourceTrait("default")));
    WorkflowMultiBranchProject mp = r.jenkins.createProject(WorkflowMultiBranchProject.class, "p");
    mp.getSourcesList().add(new BranchSource(s));
    WorkflowJob p = PipelineTest.scheduleAndFindBranchProject(mp, "default");
    assertEquals(1, mp.getItems().size());
    r.waitUntilNoActivity();
    WorkflowRun b = p.getLastBuild();
    assertNotNull(b);
    r.assertBuildStatusSuccess(b);
    assertNotNull(s.fetch("default", StreamTaskListener.fromStderr(), p));
    sampleRepo.deleteRecursive();
    assertEquals(p, PipelineTest.scheduleAndFindBranchProject(mp, "default"));
  }
}