package com.willwinder.ugs.nbp.designer.platform;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import java.io.IOException;

@MIMEResolver.ExtensionRegistration(displayName = "UGS design", mimeType = "application/x-ugs", extension = { "ugsd", "UGSD" }, position = 
<<<<<<< /usr/src/app/output/winder/universal-g-code-sender/dcb31a9394922bdde2c884feba593d110b20380e/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/platform/UgsDataObject.java/left.java
100
=======
2
>>>>>>> /usr/src/app/output/winder/universal-g-code-sender/dcb31a9394922bdde2c884feba593d110b20380e/ugs-platform/ugs-platform-plugin-designer/src/main/java/com/willwinder/ugs/nbp/designer/platform/UgsDataObject.java/right.java
) @DataObject.Registration(mimeType = "application/x-ugs", iconBase = "com/willwinder/ugs/nbp/designer/platform/edit.png", displayName = "UGS design", position = 300) public class UgsDataObject extends MultiDataObject {
  public UgsDataObject(FileObject pf, MultiFileLoader loader) throws IOException {
    super(pf, loader);
    CookieSet cookies = getCookieSet();
    cookies.add(new UgsCloseCookie(this));
    cookies.add(new UgsOpenSupport(getPrimaryEntry()));
  }

  @Override protected int associateLookup() {
    return 1;
  }

  @Override public void setModified(boolean isModified) {
    if (isModified) {
      if (getCookie(UgsSaveCookie.class) == null) {
        getCookieSet().add(new UgsSaveCookie(this));
      }
    } else {
      SaveCookie cookie = getCookie(UgsSaveCookie.class);
      if (cookie != null) {
        getCookieSet().remove(cookie);
      }
    }
    super.setModified(isModified);
  }
}