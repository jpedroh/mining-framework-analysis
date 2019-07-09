/**
 * ios-maven-plugin
 * <p>
 * User: cwack
 * Date: 2016-06-08
 * <p>
 * This code is copyright (c) 2016 let's dev.
 * URL: http://www.letsdev.de
 * e-Mail: contact@letsdev.de
 */

package de.letsdev.maven.plugins.ios.mojo;

import de.letsdev.maven.plugins.ios.CommandHelper;
import de.letsdev.maven.plugins.ios.ProjectBuilder;
import de.letsdev.maven.plugins.ios.ProvisioningProfileData;
import de.letsdev.maven.plugins.ios.ProvisioningProfileHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.letsdev.maven.plugins.ios.mojo.container.FileReplacement;
import de.letsdev.maven.plugins.ios.Utils;
import de.letsdev.maven.plugins.ios.mojo.container.StringReplacementConfig;
import de.letsdev.maven.plugins.ios.mojo.container.XcodeExportOptions;

public class BaseMojo extends AbstractMojo {

    private String currentXcodeVersion = null;

    /**
     * iOS Source Directory
     *
     * @parameter property="ios.sourceDir"
     * default-value="src/ios"
     */
    protected String sourceDir;

    /**
     * iOS app name
     *
     * @parameter property="ios.appName"
     * @required
     */
    protected String appName;

    /**
     * classifier
     *
     * @parameter property="ios.classifier"
     */
    protected String classifier;

    /**
     * iOS project name
     *
     * @parameter property="ios.projectName"
     */
    protected String projectName;

    /**
     * iOS provisioning profile  UUID
     * <p>
     * If not set the default provisioning file in xcode project will be used.
     *
     * @parameter property="ios.provisioningProfileUUID"
     */
    protected String provisioningProfileUUID;

    /**
     * iOS provisioning profile specifier
     *
     * @parameter property="ios.provisioningProfileSpecifier"
     */
    protected String provisioningProfileSpecifier;

    /**
     * iOS development team
     *
     * @parameter property="ios.developmentTeam"
     */
    protected String developmentTeam;

    /**
     * iOS bundle identifier
     * <p>
     * If not set the bundle identifier in the info.plist will be used
     *
     * @parameter property="ios.bundleIdentifier"
     */
    protected String bundleIdentifier;

    /**
     * iOS display name
     * <p>
     * If not set the display name in the info.plist will be used
     *
     * @parameter property="ios.displayName"
     */
    protected String displayName;

    /**
     * iOS scheme. This is necessary for xcarchive builds.
     * <p>
     * Scheme must be set to "share" into the xcode project!
     *
     * @parameter property="ios.scheme"
     */
    protected String scheme;

    /**
     * iOS SDK
     *
     * @parameter property="ios.sdk"
     * default-value="iphoneos"
     */
    protected String sdk;

    /**
     * iphoneos SDK build architectures
     *
     * @parameter property="ios.iphoneosArchitectures"
     */
    protected String iphoneosArchitectures;

    /**
     * iphonesimulator SDK build architectures
     * available architectures: i386 x86_64
     *
     * @parameter property="ios.iphonesimulatorArchitectures"
     * default-value="x86_64"
     */
    protected String iphonesimulatorArchitectures = "x86_64";

    /**
     * flag for bitcode enabled option for builds with iphonesimulator sdk
     *
     * @parameter property="ios.iphonesimulatorBitcodeEnabled"
     * default-value="true"
     */
    protected boolean iphonesimulatorBitcodeEnabled = true;

    /**
     * flag for iOS framework builds
     *
     * @parameter property="ios.iOSFrameworkBuild"
     * default-value="false"
     */
    protected boolean iOSFrameworkBuild;

    /**
     * flag for macosx framework builds
     *
     * @parameter property="ios.macOSFrameworkBuild"
     * default-value="false"
     */
    protected boolean macOSFrameworkBuild;

    /**
     * flag for iOS code signing enabled
     *
     * @parameter property="ios.codeSigningEnabled"
     */
    protected boolean codeSigningEnabled;

    /**
     * flag for iOS export to xcarchive enabled.
     * <p>
     * If false the .app will be generated instead of xcarchive.
     * <p>
     * You must set the xcode "scheme" value. Also the XCode scheme must be shared in the xcode project!
     * <p>
     * Default: true
     *
     * @parameter property="ios.buildXCArchiveEnabled"
     */
    protected boolean buildXCArchiveEnabled = true;

    /**
     * flag for iOS code signing with resources rules enabled
     * <p>
     * Following will be added to code sign execution:
     * <p>
     * <pre>CODE_SIGN_RESOURCE_RULES_PATH=$(SDKROOT)/ResourceRules.plist</pre>
     * <p>
     * This was necessary from iOS SDK 6.1 until 8.0
     * <p>
     * Default: false
     *
     * @parameter property="ios.codeSigningWithResourceRulesEnabled"
     */
    protected boolean codeSigningWithResourceRulesEnabled = false;

    /**
     * iOS code sign identity. The Code Sign identity, see distribution certficates common name.
     *
     * @parameter property="ios.codeSignIdentity"
     */
    protected String codeSignIdentity;

    /**
     * path to code signing entitlements file
     *
     * @parameter property="ios.codeSignEntitlements"
     */
    protected String codeSignEntitlements;

    /**
     * Path to keychain to sign with
     *
     * @parameter property="ios.keychainPath"
     */
    protected String keychainPath;

    /**
     * Password to unlock keychain to sign with
     *
     * @parameter property="ios.keychainPassword"
     */
    protected String keychainPassword;

    /**
     * iOS configuration, Release or Debug
     *
     * @parameter property="ios.configuration"
     * default-value="Release"
     */
    protected String configuration;

    /**
     * build id, will be set into info.plist
     *
     * @parameter property="ios.buildId"
     */
    protected String buildId;

    /**
     * target. The XCode target. See also "scheme".
     * <p>
     * If building apps with xcarchive, you must use "scheme" instead of target, then given target will be ignored.
     *
     * @parameter property="ios.target"
     */
    protected String target;

    /**
     * infoPlist
     *
     * @parameter property="ios.infoPlist"
     */
    protected String infoPlist;

    /**
     * app icon name
     *
     * @parameter property="ios.appIconName"
     */
    protected String appIconName;

    /**
     * gccPreprocessorDefinitions, added to xcodebuild command
     *
     * @parameter property="ios.gccPreprocessorDefinitions"
     */
    protected String gccPreprocessorDefinitions;

    /**
     * ipaVersion
     *
     * @parameter property="ios.ipaVersion"
     */
    protected String ipaVersion;

    /**
     * determines if project uses cocoapods, dependencies will be installed (via pod install) and .xcworkspace will
     * be built instead of .xcodeproj
     *
     * @parameter
     */
    protected String cocoaPodsEnabled;

    /**
     * determines if project uses carthage, dependencies will be installed (via carthage update)
     *
     * @parameter
     */
    protected String carthageEnabled;

    /**
     * defining release task
     * available options are Release, Testflight & AppStoreUpload
     * <p>
     * property can also be set via environment variable RELEASE_TASK
     *
     * @parameter property="ios.releaseTask"
     * default-value="Release"
     */
    protected String releaseTask;

    /**
     * defines the path to the xcode version, which will be used for the build process. The given path will be used
     * for the xcode-select --switch command
     * e.g. path looks like that: /Applications/Xcode.app
     *
     * @parameter property="ios.xcodeVersion"
     */
    protected String xcodeVersion;

    /**
     * defining the scheme for the xctest execution
     *
     * @parameter property="ios.xcTestsScheme"
     */
    protected String xcTestsScheme;

    /**
     * defining the configuration for the xctest execution
     *
     * @parameter property="ios.xcTestsConfiguration"
     * default-value="Debug"
     */
    protected String xcTestsConfiguration;

    /**
     * defining the destination for the xctest execution
     * e.g. platform=iOS Simulator,name=iPhone 5,OS=9.1
     *
     * @parameter property="ios.xcTestsDestination"
     */
    protected String xcTestsDestination = "platform=iOS Simulator,name=iPhone X,OS=latest";

    /**
     * defining the sdk for xctests execution
     *
     * @parameter property="ios.xcTestsSdk"
     * default-value="iphonesimulator"
     */
    protected String xcTestsSdk;

    /**
     * defining further arguments for xctests execution
     *
     * @parameter property="ios.xcTestsBuildArguments"
     * default-value="GCC_SYMBOLS_PRIVATE_EXTERN=NO COPY_PHASE_STRIP=NO"
     */
    protected String xcTestsBuildArguments;

    /**
     * defining the path for the build folder
     *
     * @parameter
     */
    protected String derivedDataPath;

    /**
     * defining the path for the build folder when you run tests
     *
     * @parameter
     */
    protected String xcTestsDerivedDataPath;

    /**
     * defining if simulators should be resetted
     *
     * @parameter property="ios.resetSimulators"
     */
    protected boolean resetSimulators = true;

    /**
     * defining all files and directories to replace
     *
     * @parameter property="ios.fileReplacements"
     */
    protected List<FileReplacement> fileReplacements;

    /**
     * defining all files and directories to replace
     *
     * @parameter property="ios.stringReplacements"
     */
    protected StringReplacementConfig stringReplacements;

    /**
     * defining parameters passed to the xcodebuild
     *
     * @parameter property="ios.xcodeBuildParameters"
     */
    protected List<String> xcodeBuildParameters;

    /**
     * defining parameters passed to the export options plist file
     *
     * @parameter property="ios.xcodeExportOptions"
     * * default-value="null"
     */
    protected XcodeExportOptions xcodeExportOptions;

    /**
     * defining target dependencies of the project, it will be bundled in the artefact
     *
     * @parameter property="ios.targetDependencies"
     */
    protected List<String> targetDependencies;

    /**
     * The maven project.
     *
     * @parameter property="project"
     * @required
     * @readonly
     */
    protected MavenProject mavenProject;

    /**
     * The filename of the provisioning profile
     *
     * @parameter
     */
    protected String provisioningProfileName;

    protected Map<String, String> properties = null;

    protected Map<String, String> prepareProperties() {

        Map<String, String> properties = new HashMap<String, String>();

        final String targetDir = this.mavenProject.getBuild().getDirectory();

        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.APP_NAME.toString(), this.appName);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.APP_ICON_NAME.toString(), this.appIconName);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.PROJECT_NAME.toString(), this.projectName);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.GCC_PREPROCESSOR_DEFINITIONS.toString(),
                this.gccPreprocessorDefinitions);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.IPHONESIMULATOR_ARCHITECTURES.toString(),
                this.iphonesimulatorArchitectures);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.IPHONESIMULATOR_BITCODE_ENABLED.toString(),
                Boolean.toString(this.iphonesimulatorBitcodeEnabled));
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.IPHONEOS_ARCHITECTURES.toString(),
                this.iphoneosArchitectures);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.IOS_FRAMEWORK_BUILD.toString(),
                Boolean.toString(this.iOSFrameworkBuild));
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.MACOSX_FRAMEWORK_BUILD.toString(),
                Boolean.toString(this.macOSFrameworkBuild));
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.CODE_SIGNING_ENABLED.toString(),
                Boolean.toString(this.codeSigningEnabled));
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.BUILD_TO_XCARCHIVE_ENABLED.toString(),
                Boolean.toString(this.buildXCArchiveEnabled));
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.CODE_SIGN_WITH_RESOURCE_RULES_ENABLED.toString(),
                Boolean.toString(this.codeSigningWithResourceRulesEnabled));
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.CODE_SIGN_IDENTITY.toString(), this.codeSignIdentity);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.CODE_SIGN_ENTITLEMENTS.toString(),
                this.codeSignEntitlements);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.SDK.toString(), this.sdk);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.SOURCE_DIRECTORY.toString(), this.sourceDir);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.TARGET_DIR.toString(), targetDir);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.CONFIGURATION.toString(), this.configuration);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.BUILD_ID.toString(), this.buildId);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.SCHEME.toString(), this.scheme);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.TARGET.toString(), this.target);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.KEYCHAIN_PATH.toString(), this.keychainPath);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.KEYCHAIN_PASSWORD.toString(), this.keychainPassword);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.INFO_PLIST.toString(), this.infoPlist);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.IPA_VERSION.toString(), this.ipaVersion);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.PROVISIONING_PROFILE_UUID.toString(),
                this.provisioningProfileUUID);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.PROVISIONING_PROFILE_SPECIFIER.toString(),
                this.provisioningProfileSpecifier);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.DEVELOPMENT_TEAM.toString(), this.developmentTeam);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.BUNDLE_IDENTIFIER.toString(), this.bundleIdentifier);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.DISPLAY_NAME.toString(), this.displayName);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.CLASSIFIER.toString(), this.classifier);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.COCOA_PODS_ENABLED.toString(), this.cocoaPodsEnabled);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.CARTHAGE_ENABLED.toString(), this.carthageEnabled);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.RELEASE_TASK.toString(), this.releaseTask);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.XCODE_VERSION.toString(), this.xcodeVersion);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.XCTEST_SCHEME.toString(), this.xcTestsScheme);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.XCTEST_CONFIGURATION.toString(),
                this.xcTestsConfiguration);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.XCTEST_DESTINATION.toString(), this.xcTestsDestination);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.XCTEST_SDK.toString(), this.xcTestsSdk);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.XCTEST_BUILD_ARGUMENTS.toString(),
                this.xcTestsBuildArguments);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.RESET_SIMULATORS.toString(),
                Boolean.toString(this.resetSimulators));
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.DERIVED_DATA_PATH.toString(), this.derivedDataPath);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.XCTEST_DERIVED_DATA_PATH.toString(),
                this.xcTestsDerivedDataPath);
        this.addProperty(properties, Utils.PLUGIN_PROPERTIES.PROVISIONING_PROFILE_NAME.toString(),
                this.provisioningProfileName);

        return properties;
    }

    protected void addProperty(Map<String, String> properties, String key, String value) {

        if (properties != null && key != null && value != null) {
            properties.put(key, value);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        this.properties = prepareProperties();

        try {
            String projectName = Utils.buildProjectName(properties, mavenProject);
            String schemeName = properties.get(Utils.PLUGIN_PROPERTIES.SCHEME.toString());
            File projectDirectory = Utils.getWorkDirectory(properties, mavenProject, projectName);
            this.currentXcodeVersion = Utils.getCurrentXcodeVersion(projectDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (properties.get(Utils.PLUGIN_PROPERTIES.XCODE_VERSION.toString()) != null && !properties.get(
                Utils.PLUGIN_PROPERTIES.XCODE_VERSION.toString()).isEmpty()) {
            try {
                selectXcodeVersion(properties.get(Utils.PLUGIN_PROPERTIES.XCODE_VERSION.toString()),
                        Utils.getWorkDirectory(properties, mavenProject, projectName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (this.provisioningProfileName != null && !this.provisioningProfileName.equals("")) {
            ProvisioningProfileHelper helper = new ProvisioningProfileHelper(this.provisioningProfileName, properties,
                    mavenProject);
            try {
                ProvisioningProfileData data = helper.getData();
                this.xcodeExportOptions.provisioningProfiles = new HashMap<>();
                this.xcodeExportOptions.provisioningProfiles.put("bundleIdentifier", data.getUuid());
                this.provisioningProfileSpecifier = null;
                this.xcodeExportOptions.teamID = data.getTeamID();
                this.xcodeExportOptions.method = data.getType().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void selectXcodeVersion(String xcodeVersionPath, File workDirectory) throws IOSException {
        // Run shell-script from resource-folder.
        try {
            final String scriptName = "set-xcode-version.sh";
            File tempFile = File.createTempFile(scriptName, "sh");

            InputStream inputStream = ProjectBuilder.class.getResourceAsStream("/META-INF/" + scriptName);
            OutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();

            ProcessBuilder processBuilder = new ProcessBuilder("sh", tempFile.getAbsoluteFile().toString(),
                    xcodeVersionPath);

            processBuilder.directory(workDirectory);
            CommandHelper.performCommand(processBuilder);
            System.out.println("############################################################################");
            System.out.println("################################ set " + xcodeVersionPath
                    + " as current xcode version ################################ set ");
            System.out.println("############################################################################");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOSException(e);
        }
    }

    protected void resetXcodeVersion(File workDirectory) throws IOSException {

        try {
            if (currentXcodeVersion != null) {
                selectXcodeVersion(currentXcodeVersion, Utils.getWorkDirectory(properties, mavenProject, projectName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}