<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>

citygrid.common.loadWidget(
<div class="ctsrch_wideContainer">
    <div class="ctsrch_sponserText">
        Ads by CityGrid
    </div>
    <div class="ctsrch_container">
        <s:iterator value="nearbyPlaces" status="placesStatus">
            <div class="ctsrch_listing">
                <div class="ctsrch_leftSide">
                    <div class="ctsrch_bigStar">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a href='<s:property value="callBackFunction" />'><img src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="listingUrl" />' ><img src='<s:property value="adImageURL" />' border="0"/></a>
                        </s:else>
                    </div>
                    <div class="ctsrch_milesFont ctsrch_reviewFont">
                        <s:property value="distance" /> mi away
                    </div>
                </div>
                <div class="ctsrch_rightSide">
                    <div class="ctsrch_mainLink">
                        <s:if test="%{isValidCallbackFunction == true}">
                            <a class="ctsrch_busNameFont" href='<s:property value="callBackFunction" />'><s:property value="name" /></a>
                        </s:if>
                        <s:else>
                            <a href='<s:property value="listingUrl" />' ><s:property value="name" /></a>
                        </s:else>
                    </div>
                    <s:if test="%{ratings > 2.5}">
                        <div class="ctsrch_starContainer">
                            <div class="ctsrch_stars">
                                <s:iterator value="rating"><s:if test="%{2}"><img src='<s:property value="resourceRootPath"/>/static/img/Star.png' class="ctsrch_starImg" border="0"/></s:if><s:elseif test="%{1}"><img src='<s:property value="resourceRootPath"/>/static/img/HalfStar.png' class="ctsrch_starImg" border="0"/></s:elseif><s:else><img src='<s:property value="resourceRootPath"/>/static/img/EmptyStar.png' class="ctsrch_starImg" border="0"/></s:else></s:iterator>
                            </div>
                            <s:if test="%{reviewCount > 0}">
                                <div class="ctsrch_reviewFont">
                                    <s:property value="reviewCount" /> Reviews
                                </div>
                            </s:if>
                        </div>
                    </s:if>
                    <s:if test="%{isValidLocation == true}">
                        <div class="ctsrch_cityFont">
                            <s:property value="location" />
                        </div>
                    </s:if>
                    <div class="ctsrch_subcategoryFont">
                        <s:property value="category" />
                    </div>
                </div>
            </div>
        </s:iterator>
    </div>
</div>
);