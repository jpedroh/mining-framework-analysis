package org.jboss.seam.render;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.el.Expressions;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(value = Arquillian.class) public abstract class RenderTestBase {
  @Deployment public static JavaArchive createTestArchive() {
    JavaArchive deployment = ShrinkWrap.create(JavaArchive.class).addPackages(true, Root.class.getPackage()).addPackages(
<<<<<<< /usr/src/app/output/seam/render/5371832457e9d1037dba8e7091513b6521e5af80/src/test/java/org/jboss/seam/render/RenderTestBase.java/left.java
    false
=======
    true
>>>>>>> /usr/src/app/output/seam/render/5371832457e9d1037dba8e7091513b6521e5af80/src/test/java/org/jboss/seam/render/RenderTestBase.java/right.java
    , Expressions.class.getPackage()).addManifestResource(new ByteArrayAsset("<beans/>".getBytes()), ArchivePaths.create("beans.xml")).addManifestResource("META-INF/services/org.jboss.seam.solder.beanManager.BeanManagerProvider");
    return deployment;
  }
}