/*
 * Copyright (c) 2011 Adobe Systems Incorporated
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.adobe.epubcheck.api;

import com.adobe.epubcheck.messages.MessageId;
import java.util.Collections;
import java.util.Locale;
import org.junit.Test;


public class Epub30CheckExpandedTest extends AbstractEpubCheckTest
{

  public Epub30CheckExpandedTest()
  {
    super("/30/expanded/");
  }

  @Test
  public void testValidateEPUBPLoremBasic()
  {
    testValidateDocument("valid/lorem-basic", "valid/lorem-basic.txt");
  }

  @Test
  public void testDuplicateID()
  {
    // 2 errors x 2 sets of duplicate IDs
    Collections.addAll(expectedErrors, MessageId.RSC_005, MessageId.RSC_005, MessageId.RSC_005,
        MessageId.RSC_005);
    testValidateDocument("invalid/duplicate-id");
  }

  @Test
  public void testValidateEPUBPLoremMultipleRenditionsUnmanifested()
  {
    Collections.addAll(expectedWarnings, MessageId.RSC_019, MessageId.RSC_017, MessageId.OPF_003);
    testValidateDocument("invalid/lorem-xrenditions-unmanifested");
  }

  @Test
  public void testValidateEPUBWastelandBasic()
  {
    testValidateDocument("valid/wasteland-basic", "valid/wasteland-basic.txt");
  }

  @Test
  public void testValidateEPUBLoremAudio()
  {
    testValidateDocument("valid/lorem-audio", "valid/lorem-audio.txt");
  }

  @Test
  public void testValidateEPUBLoremxhtmlrng1()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/lorem-xhtml-rng-1");
  }

  @Test
  public void testValidateEPUBLoremxhtmlsch1()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/lorem-xhtml-sch-1");
  }

  @Test
  public void testValidateEPUBPLoremBasicMathml()
  {
    testValidateDocument("valid/lorem-basic-mathml");
  }

  @Test
  public void testValidateEPUBPLoremMimetype()
  {
    Collections.addAll(expectedErrors, MessageId.PKG_007);
    testValidateDocument("invalid/lorem-mimetype");
  }

  @Test
  public void testValidateEPUBPLoremMimetype2()
  {
    Collections.addAll(expectedErrors, MessageId.PKG_007);
    testValidateDocument("invalid/lorem-mimetype-2");
  }

  @Test
  public void testValidateEPUBPLoremLink()
  {
    testValidateDocument("valid/lorem-link", "valid/lorem-link.txt");
  }

  @Test
  public void testValidateEPUBPLoremForeign()
  {
    testValidateDocument("valid/lorem-foreign", "valid/lorem-foreign.txt");
  }

  @Test
  public void testFallbackNativeForObject()
  {
    // tests that 'object' with a native fallback (inner content) is acceptable
    testValidateDocument("valid/fallback-object-native", "valid/fallback-object-native.txt");
  }

  @Test
  public void testFallbackBindingsForObject()
  {
    // tests that bindings provide an acceptable fallback
    // warning raised as bindings are deprecated
    Collections.addAll(expectedWarnings, MessageId.RSC_017);
    testValidateDocument("valid/fallback-bindings", "valid/fallback-bindings.txt");
  }

  @Test
<<<<<<< LEFT
  public void testFallbackNoneForObject()
=======
  public void testValidateEPUBPLoremBindingsWithNativeFallback()
>>>>>>> RIGHT
  {
<<<<<<< LEFT
    // tests that an object with no fallback is reported as an error
    Collections.addAll(expectedErrors, MessageId.MED_002);
    testValidateDocument("invalid/fallback-object-none");
=======
    // tests that an object element with both bindings and native fallback is allowed
    testValidateDocument("valid/lorem-bindings-withnativefallback");
>>>>>>> RIGHT
  }

  @Test
  public void testValidateEPUBPLoremPoster()
  {
    testValidateDocument("valid/lorem-poster", "valid/lorem-poster.txt");
  }

  @Test
  public void testValidateEPUBPLoremSvg()
  {
    testValidateDocument("valid/lorem-svg", "valid/lorem-svg.txt");
  }

  @Test
  public void testImagePNG()
  {
    testValidateDocument("valid/image-png");
  }

  @Test
  public void testImageJPG()
  {
    testValidateDocument("valid/image-jpg");
  }

  @Test
  public void testValidateEPUBPLoremSvgHyperlink()
  {
    testValidateDocument("valid/lorem-svg-hyperlink", "valid/lorem-svg-hyperlink.txt");
  }

  @Test
  public void testValidateEPUBPLoremSvgHyperlinkNoTitle()
  {
    Collections.addAll(expectedWarnings, MessageId.ACC_011);
    testValidateDocument("invalid/lorem-svg-hyperlink-notitle");
  }

  @Test
  public void testValidateEPUBPLoremIFrame()
  {
    testValidateDocument("valid/lorem-iframe");
  }

  @Test
  public void testValidateEPUBPInvalidLoremPoster()
  {
    Collections.addAll(expectedErrors, MessageId.MED_001);
    testValidateDocument("invalid/lorem-poster");
  }

  @Test
  public void testValidateEPUBPInvalidLoremForeign()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_014);
    testValidateDocument("invalid/lorem-foreign");
  }

  @Test
  public void testValidateEPUB30_navInvalid()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/nav-invalid/");
  }

  @Test
  public void testValidateEPUB30_issue134_1()
  {
    // svg in both contentdocs, opf props set right
    testValidateDocument("valid/lorem-svg-dual/", "valid/lorem-svg-dual.txt");
  }

  @Test
  public void testValidateEPUB30_issue134_2()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_014, MessageId.OPF_014);
    // svg in both contentdocs, no opf props set right
    testValidateDocument("invalid/lorem-svg-dual/");
  }

  @Test
  public void testValidateEPUB30_issue134_3()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_014);
    // svg in both contentdocs, only one opf prop set right
    testValidateDocument("invalid/lorem-svg-dual-2/");
  }

  @Test
  public void testValidateEPUB30_CSSImport_valid()
  {
    testValidateDocument("valid/lorem-css-import/", "valid/lorem-css-import.txt");
  }

  @Test
  public void testValidateEPUB30_CSSImport_invalid_1()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_001);
    testValidateDocument("invalid/lorem-css-import-1/");
  }

  @Test
  public void testValidateEPUB30_CSSImport_invalid_2()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_008);
    Collections.addAll(expectedWarnings, MessageId.OPF_003);
    testValidateDocument("invalid/lorem-css-import-2/");
  }

  @Test
  public void testValidateEPUB30_CSSURLS_1()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_007);
    // 'imgs/table_header_bg_uni.jpg': referenced resource missing in the
    // package
    testValidateDocument("invalid/lorem-css-urls-1/");
  }

  @Test
  public void testValidateEPUB30_CSSURLS_2()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_027, MessageId.CSS_020, MessageId.CSS_020);
    // 'imgs/table_header_bg_uni.jpg': referenced resource missing in the
    // package
    testValidateDocument("invalid/lorem-css-urls-2/");
  }

  @Test
  public void testValidateEPUB30_CSSURLS_3()
  {
    Collections.addAll(expectedWarnings, MessageId.CSS_017);
    Collections.addAll(expectedErrors, MessageId.CSS_020, MessageId.CSS_020);
    // 'imgs/table_header_bg_uni.jpg': referenced resource missing in the
    // package
    testValidateDocument("invalid/lorem-css-urls-3/");
  }

  @Test
  public void testValidateEPUB30_CSSFontFace_valid()
  {
    testValidateDocument("valid/wasteland-otf/", "valid/wasteland-otf.txt");
  }

  @Test
  public void testValidateEPUB30_CSSFontFace_invalid()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_001, MessageId.RSC_001, MessageId.RSC_001);
    // referenced fonts missing
    testValidateDocument("invalid/wasteland-otf/");
  }

  @Test
  public void testValidateEPUB30_CSSEncoding_invalid()
  {
    Collections.addAll(expectedErrors, MessageId.CSS_003);
    // @charset not utf
    testValidateDocument("invalid/lorem-css-enc/");
  }

  @Test
  public void testValidateEPUB30_CSSMediaType_invalid()
  {
    Collections.addAll(expectedErrors, MessageId.CSS_010);
    // CSS with declared type 'xhtml/css' should raise a "no fallback" error
    testValidateDocument("invalid/lorem-css-wrongtype/");
  }

  @Test
  public void testRemoteAudio()
  {
    // tests that remote audio resources are allowed
    testValidateDocument("valid/remote-audio/");
  }

  @Test
  public void testRemoteAudioSources()
  {
    // tests that remote audio resources defined in the 'sources' element are
    // allowed
    testValidateDocument("valid/remote-audio-sources/");
  }
  
  @Test
  public void testRemoteAudioSourcesOfForeignType()
  {
    // tests that remote audio resources are allowed, even with foreign types
    testValidateDocument("valid/remote-audio-sources-foreign/");
  }

  @Test
  public void testRemoteVideo()
  {
    // tests that remote video resources are allowed
    testValidateDocument("valid/remote-video/");
  }

  @Test
  public void testRemoteIframe()
  {
    // tests that remote iframes are not allowed
    // See #852
    Collections.addAll(expectedErrors, MessageId.RSC_006);
    testValidateDocument("invalid/remote-iframe/");
  }

  @Test
  public void testRemoteIframeUndeclaredInOPF()
  {
    // tests that remote iframes are not allowed, even when not declared in OPF
    Collections.addAll(expectedErrors, MessageId.RSC_006);
    testValidateDocument("invalid/remote-iframe-undeclared/");
  }

  @Test
  public void testRemoteImg()
  {
    // tests that remote images are not allowed
    Collections.addAll(expectedErrors, MessageId.RSC_006);
    testValidateDocument("invalid/remote-img/");
  }

  @Test
  public void testRemoteImgUndeclaredInOPF()
  {
    // tests that remote images are not allowed, even when not declared in OPF
    Collections.addAll(expectedErrors, MessageId.RSC_006);
    testValidateDocument("invalid/remote-img-undeclared/");
  }

  @Test
  public void testRemoteAudioWithMissingRemoteResourcesProperty()
  {
    // tests that the 'remote-resources' property is required in OPF for
    // content referencing remote resources
    Collections.addAll(expectedErrors, MessageId.OPF_014);
    testValidateDocument("invalid/remote-audio-missingproperty/");
  }

  @Test
  public void testRemoteAudioUndeclaredInOPF()
  {
    // tests that remote audio resources must be declared in the OPF
    // - RSC_008 is raised since the resorue is not declared
    // - MED_002 is a side-effect error about the audio missing a fallback
    // (since its type cannot be known from the OPF declaration)
    Collections.addAll(expectedErrors, MessageId.MED_002, MessageId.RSC_008);
    testValidateDocument("invalid/remote-audio-undeclared/");
  }

  @Test
  public void testRemoteAudioSourcesUndeclaredInOPF()
  {
    // tests that remote audio resources defined in 'sources' elements
    // must be declared in the OPF
    Collections.addAll(expectedErrors, MessageId.RSC_008);
    testValidateDocument("invalid/remote-audio-sources-undeclared/");
  }
  
  @Test
  public void testValidateEPUB30_circularFallback()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_045, MessageId.OPF_045, MessageId.OPF_045,
        MessageId.OPF_045, MessageId.MED_003);
    testValidateDocument("invalid/fallbacks-circular/");
  }

  @Test
  public void testValidateEPUB30_nonresolvingFallback()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005, MessageId.MED_003);
    // dupe messages, tbf
    testValidateDocument("invalid/fallbacks-nonresolving/");
  }

  @Test
  public void testValidateEPUB30_okFallback()
  {
    testValidateDocument("valid/fallbacks/", "valid/fallbacks.txt");
  }

  @Test
  public void testValidateEPUB30_svgCoverImage()
  {
    testValidateDocument("valid/svg-cover/", "valid/svg-cover.txt");
  }

  @Test
  public void testValidateEPUB30_svgInSpine()
  {
    // svg in spine, svg cover image
    testValidateDocument("valid/svg-in-spine/", "valid/svg-in-spine.txt");
  }

  @Test
  public void testValidateEPUB30_svgReferenced()
  {
    // svg referenced from img, object, iframe
    testValidateDocument("valid/svg-referenced/");
  }

  @Test
<<<<<<< LEFT
=======
  public void testValidateEPUB30_svgSwitch()
  {
    // tests that svg:switch doesn't trigger the OPF 'switch' property check
    testValidateDocument("valid/svg-switch/");
  }

  @Test
  public void testValidateEPUB30_videoAudioTrigger()
  {
    testValidateDocument("valid/cc-shared-culture/", "valid/cc-shared-culture.txt");
  }

  @Test
>>>>>>> RIGHT
  public void testValidateEPUB30_InvalidLinks()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_007, MessageId.RSC_012, MessageId.RSC_012,
        MessageId.RSC_012);
    /*
     * the valid counterpart is in the zipped section
     * 
     * - one broken file ref in navdoc - one broken frag ref in navdoc - one
     * broken internal frag ref in overview - one broken crossdoc frag ref in
     * overview
     */

    testValidateDocument("invalid/epub30-spec/");
  }

  @Test
  public void testValidateEPUB30_basicDual()
  {
    testValidateDocument("valid/lorem-basic-dual/", "valid/lorem-basic-dual.txt");
  }

  @Test
  public void testValidateEPUB30_Base()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_006, MessageId.RSC_006);
    // <base href set, see issue 155
    testValidateDocument("invalid/lorem-basic-dual-base/");
  }

  @Test
  public void testValidateEPUB30_InvalidContainer()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/lorem-container/");
  }

  @Test
  public void testValidateEPUB30_InvalidSignatures()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/lorem-signatures/");
  }

  @Test
  public void testValidateEPUB30_InvalidEncryption()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/lorem-encryption/");
  }

  @Test
  public void testValidateEPUB30_customNsAttr()
  {
    testValidateDocument("invalid/custom-ns-attr/");
  }
  
  /**
   * Also tests locale-independent character case transformations (such as
   * lower-casing). Specifically, in issue 711, when the default locale is set
   * to Turkish, lower-casing resulted in incorrect vocabulary strings (for
   * "PAGE_LIST" enum constant name relevant to the original issue report, as
   * well as for numerous other strings). Therefore, a Turkish locale is set as
   * the default at the beginning of the test (the original locale is restored
   * at the end of the test).
   */
  @Test
  public void testPageList()
  {
    Locale previousLocale = Locale.getDefault();
    try {
      // E.g., tests that I is not lower-cased to \u0131 based on locale's collation rules:
      Locale.setDefault(new Locale("tr", "TR"));
      testValidateDocument("valid/page-list");
    } finally { // restore the original locale
      Locale.setDefault(previousLocale);
    }
  }

  @Test
  public void testIssue188()
  {
    // Image file name containing '+'
    testValidateDocument("valid/issue188/");
  }

  @Test
  public void testIssue189()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    // element "somebadxhtmlformatting" not allowed here
    testValidateDocument("invalid/issue189/");
  }

  @Test
  public void testIssue198()
  {
    // Collections.addAll(expectedErrors, );

    // also data-* removal
    testValidateDocument("valid/issue198/");
  }

  @Test
  public void testIssue225()
  {
    // 2 @href values 0-length and empty after ws norm
    // issue225 asked for warning here, but we give none
    // until we have a compat hint message type; the empty
    // string is a valid URI
    testValidateDocument("valid/issue225/");
  }

  @Test
  public void testIssue226()
  {
    testValidateDocument("valid/issue226/");
  }

  @Test
  public void testIssue237()
  {
    // namespace uri in css is not a remote resource...
    testValidateDocument("valid/issue237/");
  }

  @Test
  public void testIssue249()
  {
    // tests that video defined in object/param[@name='movie'] elements are identified as resources
    Collections.addAll(expectedWarnings, MessageId.OPF_018);
    testValidateDocument("valid/issue249/");
  }

  @Test
  public void testValidateEPUB20_issue267()
  {
    testValidateDocument("valid/issue267/", "valid/issue267.txt");
  }

  @Test
  public void testIssue270()
  {
    testValidateDocument("valid/issue270/");
  }

  @Test
  public void testIssue305()
  {
    expectedErrors.add(MessageId.OPF_073);
    testValidateDocument("invalid/ncx-external-identifier");
  }

  @Test
  public void testIssue332()
  {
    testValidateDocument("valid/issue332-idspaces");
  }

  @Test
  public void testIssue419()
  {
    testValidateDocument("valid/issue419/");
  }

  @Test
  public void testIssue5()
  {
    testValidateDocument("valid/issue567/");
  }
  
  @Test
  public void testIssue615_langtag() {
    testValidateDocument("valid/issue615-langtags/");
  }
  
  @Test
  public void testIssue922()
  {
    // tests that CSS 'font-size: 0' is accepted
    testValidateDocument("valid/issue922/");
  }

  @Test
  public void testResource_Missing() {
    Collections.addAll(expectedErrors, MessageId.RSC_001);
    testValidateDocument("invalid/resource-missing/");
  }
  
  @Test
  public void testResource_RefInXHTML_Undeclared() {
    Collections.addAll(expectedErrors, MessageId.RSC_007);
    testValidateDocument("invalid/resource-missing-refinxhtml/");
  }

  @Test
  public void testFallback_XPGT_Explicit()
  {
    testValidateDocument("valid/xpgt-explicit-fallback/");
  }

  @Test
  public void testFallback_XPGT_Implicit()
  {
    testValidateDocument("valid/xpgt-implicit-fallback/");
  }

  @Test
  public void testFallback_XPGT_NoFallback()
  {
    Collections.addAll(expectedErrors, MessageId.CSS_010);
    testValidateDocument("invalid/xpgt-no-fallback/");
  }
  
  @Test
  public void testFont_OpenType() {
    testValidateDocument("valid/font-opentype");
  }
  
  @Test
  public void testFont_NonCoreMediaType() {
    testValidateDocument("valid/font-othermediatype");
  }
  
  @Test
  public void testFXL_WithSVG() {
    testValidateDocument("valid/fxl-svg/");
  }
  
  @Test
  public void testFXL_WithSVG_NoViewbox() {
    expectedErrors.add(MessageId.HTM_048);
    testValidateDocument("invalid/fxl-svg-noviewbox/");
  }
  
  @Test
  public void testFXL_WithSVGNotInSpine() {
    testValidateDocument("valid/fxl-svg-notinspine/");
  }
  
  @Test
  public void testLink_MissingResource(){
    Collections.addAll(expectedWarnings, MessageId.RSC_007w);
    testValidateDocument("invalid/link-missing/");
  }

  @Test
  public void testMultipleRenditions()
  {
    testValidateDocument("valid/multiple-renditions");
  }

  @Test
  public void testMultipleRenditions_Mapping_MultipleNavs()
  {
    testValidateDocument("valid/multiple-renditions-mapping-multiplenavs");
  }

  @Test
  public void testMultipleRenditions_Mapping_NotXHTML()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/multiple-renditions-mapping-nonxhtml");
  }

  @Test
  public void testMultipleRenditions_Mapping_MoreThanOne()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    // side-effect or more than one mapping: only one is recognized as declared,
    // hence the OPF_003
    Collections.addAll(expectedWarnings, MessageId.OPF_003);
    testValidateDocument("invalid/multiple-renditions-multiple-mappings");
  }

  @Test
  public void testMultipleRenditions_Mapping_NoVersionMeta()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/multiple-renditions-mapping-noversion");
  }

  @Test
  public void testMultipleRenditions_Mapping_NoResourceMap()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/multiple-renditions-mapping-noresourcemap");
  }

  @Test
  public void testMultipleRenditions_Mapping_UnidentifiedNavType()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/multiple-renditions-mapping-untypednav");
  }

  @Test
  public void testPreview_Embedded()
  {
    testValidateDocument("valid/preview-embedded/");
  }

  @Test
  public void testPreview_Embedded_NoManifest()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/preview-embedded-nomanifest/");
  }

  @Test
  public void testPreview_Embedded_NoLinks()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/preview-embedded-nolinks/");
  }

  @Test
  public void testPreview_Embedded_LinkNotContentDoc()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_075);
    testValidateDocument("invalid/preview-embedded-linknoCD/");
  }

  @Test
  public void testPreview_Embedded_LinkWithCFI()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_076);
    testValidateDocument("invalid/preview-embedded-linkcfi/");
  }

  @Test
  public void testPreview_Pub()
  {
    testValidateDocument("valid/preview-pub/");
  }

  @Test
  public void testPreview_Pub_NoType()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/preview-pub-notype/", EPUBProfile.PREVIEW);
  }

  @Test
  public void testPreview_Pub_NoSource()
  {
    Collections.addAll(expectedWarnings, MessageId.RSC_017);
    testValidateDocument("invalid/preview-pub-nosource/");
  }

  @Test
  public void testPreview_Pub_SameSourceId()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/preview-pub-samesourceid/");
  }

  @Test
  public void testXHTMExtension()
  {
    Collections.addAll(expectedWarnings, MessageId.HTM_014a);
    testValidateDocument("invalid/xhtml-extension");
  }

  @Test
  public void testEdupub_Basic()
  {
    testValidateDocument("valid/edu-basic/", EPUBProfile.EDUPUB);
  }
  
  @Test
  public void testEdupub_FXL()
  {
    testValidateDocument("valid/edu-fxl/", EPUBProfile.EDUPUB);
  }

  @Test
  public void testEdupub_NonLinear()
  {
    testValidateDocument("valid/edu-non-linear/", EPUBProfile.EDUPUB);
  }

  @Test
  public void testEdupubPagination_InvalidNoPageList()
  {
    Collections.addAll(expectedErrors, MessageId.NAV_003);
    testValidateDocument("invalid/edu-pagination-nopagelist/", EPUBProfile.EDUPUB);
  }

  @Test
  public void testEdupubPagination_InvalidNoPageSource()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_066);
    testValidateDocument("invalid/edu-pagination-nopagesource/", EPUBProfile.EDUPUB);
  }

  @Test
  public void testEdupubPagination_InvalidWithMicrodata()
  {
    Collections.addAll(expectedWarnings, MessageId.HTM_051);
    testValidateDocument("invalid/edu-microdata/", EPUBProfile.EDUPUB);
  }

  @Test
  public void test_MissingLOx()
  {
    testValidateDocument("valid/non-edu-missing-lox/");
  }

  @Test
  public void testEdupub_MissingLOx()
  {
    testValidateDocument("invalid/edu-missing-lox/", EPUBProfile.EDUPUB);
  }

  @Test
  public void testIdx_SingleFile()
  {
    testValidateDocument("valid/idx-single-file/");
  }

  @Test
  public void testIdx_SingleFile_InvalidIndexContent()
  {
    expectedErrors.add(MessageId.RSC_005);
    testValidateDocument("invalid/idx-single-file-badidxcontent/");
  }

  @Test
  public void testIdx_SingleFile_InvalidNoIndex()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_015, MessageId.RSC_005);
    testValidateDocument("invalid/idx-single-file-noindex/");
  }

  @Test
  public void testIdx_WholePub()
  {
    testValidateDocument("valid/idx-whole-pub/");
  }

  @Test
  public void testIdx_WholePub_InvalidIndexContent()
  {
    expectedErrors.add(MessageId.RSC_005);
    testValidateDocument("invalid/idx-whole-pub-badidxcontent/");
  }

  @Test
  public void testIdx_WholePub_InvalidNoIndex()
  {
    expectedErrors.add(MessageId.RSC_005);
    testValidateDocument("invalid/idx-whole-pub-noindex/");
  }

  @Test
  public void testIdx_Collection()
  {
    testValidateDocument("valid/idx-collection/");
  }

  @Test
  public void testIdx_Collection_InvalidIndexContent()
  {
    expectedErrors.add(MessageId.RSC_005);
    testValidateDocument("invalid/idx-collection-badidxcontent/");
  }

  @Test
  public void testIdx_Collection_InvalidNoIndex()
  {
    expectedErrors.add(MessageId.RSC_005);
    testValidateDocument("invalid/idx-collection-noindex");
  }

  @Test
  public void testDataNav_Basic()
  {
    testValidateDocument("valid/data-nav-basic");
  }

  @Test
  public void testDataNav_MoreThanOne()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/data-nav-multiple");
  }

  @Test
  public void testDataNav_NotXHTML()
  {
    Collections.addAll(expectedErrors, MessageId.OPF_012);
    testValidateDocument("invalid/data-nav-notxhtml");
  }

  @Test
  public void testDataNav_InSpine()
  {
    Collections.addAll(expectedWarnings, MessageId.OPF_077);
    testValidateDocument("invalid/data-nav-inspine");
  }

  @Test
  public void testDataNav_MissingType()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/data-nav-missing-type");
  }

  @Test
  public void testDataNav_RegionBased()
  {
    testValidateDocument("valid/data-nav-regionbased");
  }

  @Test
  public void testDataNav_RegionBased_NotInDataNav()
  {
    Collections.addAll(expectedErrors, MessageId.HTM_052);
    testValidateDocument("invalid/data-nav-regionbased-notindatanav");
  }

  @Test
  public void testDataNav_RegionBased_NotFXL()
  {
    Collections.addAll(expectedErrors, MessageId.NAV_009);
    testValidateDocument("invalid/data-nav-regionbased-notfxl");
  }

  @Test
  public void testDataNav_RegionBased_Struct()
  {
    Collections.addAll(expectedWarnings, MessageId.RSC_017);
    Collections.addAll(expectedErrors, MessageId.RSC_005, MessageId.RSC_005, MessageId.RSC_005,
        MessageId.RSC_005, MessageId.RSC_005, MessageId.RSC_005, MessageId.RSC_005);
    testValidateDocument("invalid/data-nav-regionbased-struct");
  }
  
  @Test
  public void testDataNav_RegionBased_ComicsTypes()
  {
      testValidateDocument("valid/data-nav-regionbased-comics");
  }

  @Test
  public void testDict_Single()
  {
    testValidateDocument("valid/dict-single");
  }

  @Test
  public void testDict_Single_NoDictContent()
  {
    expectedErrors.add(MessageId.OPF_078);
    testValidateDocument("invalid/dict-single-nodictcontent");
  }

  @Test
  public void testDict_InvalidDictContent()
  {
    // Two errors: one in Nav Doc, one in regular XHTML Doc
    Collections.addAll(expectedErrors, MessageId.RSC_005, MessageId.RSC_005);
    testValidateDocument("invalid/dict-invalidcontent");
  }

  @Test
  public void testDict_SearchKeyMap_Invalid()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_005);
    testValidateDocument("invalid/dict-skm-invalid");
  }

  @Test
  public void testDict_SearchKeyMap_BadExtension()
  {
    Collections.addAll(expectedWarnings, MessageId.OPF_080);
    testValidateDocument("invalid/dict-skm-badextension");
  }

  @Test
  public void testDict_SearchKeyMap_LinkDoesntResolve()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_007);
    testValidateDocument("invalid/dict-skm-linktonowhere");
  }

  @Test
  public void testDict_SearchKeyMap_LinkToNonContentDoc()
  {
    Collections.addAll(expectedErrors, MessageId.RSC_021);
    testValidateDocument("invalid/dict-skm-linktocss");
  }

  @Test
  public void testDict_NoDCType()
  {
    // error from schema, because profile is explictly asked
    expectedErrors.add(MessageId.RSC_005);
    // warning from content being detected as dictionary
    expectedWarnings.add(MessageId.OPF_079);
    testValidateDocument("invalid/dict-nodctype", EPUBProfile.DICT);
  }

  @Test
  public void testDict_NoDCTypeButDictContent()
  {
    // Profile not set, but detected as dictionary from epub:type
    expectedWarnings.add(MessageId.OPF_079);
    testValidateDocument("invalid/dict-nodctype-2");
  }

  @Test
  public void testDict_Multiple()
  {
    testValidateDocument("valid/dict-multiple");
  }

  @Test
  public void testDict_Multiple_NoDictContent()
  {
    expectedErrors.add(MessageId.OPF_078);
    testValidateDocument("invalid/dict-multiple-nodictcontent");
  }

  @Test
  public void testDuplicateResources()
  {
    expectedErrors.add(MessageId.OPF_074);
    testValidateDocument("invalid/duplicate-resource");
  }

  @Test
  public void testEncryption_Unknown(){
    expectedErrors.add(MessageId.RSC_004);
    testValidateDocument("invalid/encryption-unknown");
  }
  
  @Test
  public void testOutOfSpineRef()
  {
    expectedErrors.add(MessageId.RSC_011);
    testValidateDocument("invalid/href-outofspine");
  }
  
  @Test
  public void testInvalidCssFontSizeValue()
  {
    Collections.addAll(expectedErrors, MessageId.CSS_020, MessageId.CSS_020, MessageId.CSS_020);
    testValidateDocument("invalid/invalid-css-font-size-value");
  }

  @Test
  public void testSwitchMissingProperty()
  {
    Collections.addAll(expectedWarnings, MessageId.RSC_017);
    Collections.addAll(expectedErrors, MessageId.OPF_014);
    testValidateDocument("invalid/switch-missing-property");
  }
  
  @Test
  public void testEntities() {
    // tests that comments and CDATA sections aren't parsed for entity references
    testValidateDocument("valid/entities-in-comment-or-cdata");
  }
  
  @Test
  public void testBaseURI()
  {
    testValidateDocument("valid/base-uri");
  }

}