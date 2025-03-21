package io.swagger.codegen.python;
import io.swagger.codegen.AbstractOptionsTest;
import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.languages.PythonClientCodegen;
import io.swagger.codegen.options.PythonClientOptionsProvider;
import mockit.Expectations;
import mockit.Tested;

public class PythonClientOptionsTest extends AbstractOptionsTest {
  @Tested private PythonClientCodegen clientCodegen;

  public PythonClientOptionsTest() {
    super(new PythonClientOptionsProvider());
  }

  @Override protected CodegenConfig getCodegenConfig() {
    return clientCodegen;
  }

  @Override protected void setExpectations() {
    new Expectations(clientCodegen) {
      {
        clientCodegen.setPackageName(PythonClientOptionsProvider.PACKAGE_NAME_VALUE);
        times = 1;
        clientCodegen.setPackageVersion(PythonClientOptionsProvider.PACKAGE_VERSION_VALUE);
        times = 1;
      }
    };
  }


<<<<<<< /usr/src/app/output/wordnik/swagger-codegen/6b4aaf8ada78a8a55d0488d820085fb024b1a364/modules/swagger-codegen/src/test/java/io/swagger/codegen/python/PythonClientOptionsTest.java/left.java
  @Override protected Map<String, String> getAvaliableOptions() {
    ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
    return builder.put(CodegenConstants.PACKAGE_NAME, PACKAGE_NAME_VALUE).put(CodegenConstants.PACKAGE_VERSION, PACKAGE_VERSION_VALUE).put(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG, "true").build();
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}