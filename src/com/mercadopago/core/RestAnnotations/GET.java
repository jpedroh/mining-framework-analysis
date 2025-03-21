
<<<<<<< /usr/src/app/output/mercadopago/sdk-java/2c9be5b677644ad12b969e41953f21f308218aa0/src/com/mercadopago/core/RestAnnotations/GET.java/left.java
package com.mercadopago.core.restannotations;
import java.lang.annotation.*;

@Inherited @Target(value = { ElementType.TYPE, ElementType.METHOD }) @Retention(value = RetentionPolicy.RUNTIME) public @interface GET {
  String path();
}
=======
package com.mercadopago.core.RestAnnotations;
import java.lang.annotation.*;

@Inherited @Target(value = { ElementType.METHOD }) @Retention(value = RetentionPolicy.RUNTIME) public @interface GET {
  String path();
}
>>>>>>> /usr/src/app/output/mercadopago/sdk-java/2c9be5b677644ad12b969e41953f21f308218aa0/src/com/mercadopago/core/RestAnnotations/GET.java/right.java
