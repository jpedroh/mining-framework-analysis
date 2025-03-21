package io.swagger.codegen.csharp;
import io.swagger.codegen.AbstractOptionsTest;
import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.languages.CSharpClientCodegen;
import io.swagger.codegen.options.CSharpClientOptionsProvider;
import mockit.Expectations;
import mockit.Tested;

public class CSharpClientOptionsTest extends AbstractOptionsTest {
  @Tested private CSharpClientCodegen clientCodegen;

  public CSharpClientOptionsTest() {
    super(new CSharpClientOptionsProvider());
  }

  @Override protected CodegenConfig getCodegenConfig() {
    return clientCodegen;
  }

  @Override protected void setExpectations() {
    new Expectations(clientCodegen) {
      {
        clientCodegen.setPackageName(CSharpClientOptionsProvider.PACKAGE_NAME_VALUE);
        times = 1;
        clientCodegen.setPackageVersion(CSharpClientOptionsProvider.PACKAGE_VERSION_VALUE);
        times = 1;
      }
    };
  }


<<<<<<< /usr/src/app/output/wordnik/swagger-codegen/6b4aaf8ada78a8a55d0488d820085fb024b1a364/modules/swagger-codegen/src/test/java/io/swagger/codegen/csharp/CSharpClientOptionsTest.java/left.java
  @Override protected Map<String, String> getAvaliableOptions() {
    ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<String, String>();
    return builder.put(CodegenConstants.PACKAGE_NAME, PACKAGE_NAME_VALUE).put(CodegenConstants.PACKAGE_VERSION, PACKAGE_VERSION_VALUE).put(CodegenConstants.SORT_PARAMS_BY_REQUIRED_FLAG, "true").build();
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.
}