/*
 * Copyright 2004 Sun Microsystems, Inc.
 * Copyright 2011 ROME Team
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.module.Extendable;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import com.sun.syndication.feed.synd.SyndPerson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jdom2.Element;


/**
 * Bean for entry elements of Atom feeds.
 * <p>
 * 
 * @author Alejandro Abdelnur
 * @author Dave Johnson (updated for Atom 1.0)
 */
public class Entry implements Cloneable , Serializable , Extendable {
    private static final long serialVersionUID = 4874483180016783939L;

    private Content summary;

    private Content title;

    // Atom 0.3 only
    private Date created; // Atom 0.3 only

    // AKA issued
    private Date published; // AKA issued

    // AKA modified
    private Date updated; // AKA modified

    private Feed source;

    private List<Link> alternateLinks;

    private List<SyndPerson> authors;

    private List<Category> categories;

    private List<Content> contents;

    private List<SyndPerson> contributors;

    private List<Element> foreignMarkup;

    private List<Module> modules;

    private List<Link> otherLinks;

    private final ObjectBean objBean;

    private String id;

    private String rights;

    private String xmlBase;

    /**
     * Default constructor. All properties are set to <b>null</b>.
     * <p>
     */
    public Entry() {
        objBean = new ObjectBean(this.getClass(), this);
    }

    /**
     * Sets the entry alternate links.
     * <p>
     * 
     * @param alternateLinks the list of Link elements with the entry alternate
     *            links to set, an empty list or <b>null</b> if none.
     */
    public void setAlternateLinks(final List<Link> alternateLinks) {
        this.alternateLinks = alternateLinks;
    }

    /**
     * Returns the entry alternate links.
     * <p>
     * 
     * @return a list of Link elements with the entry alternate links, an empty
     *         list if none.
     */
    public List<Link> getAlternateLinks() {
        if (alternateLinks == null) {
            alternateLinks = new ArrayList<Link>();
        }
        return alternateLinks;
    }

    /**
     * Sets the author of the entry.
     * <p>
     * 
     * @param authors the author of the entry, <b>null</b> if none.
     * 
     */
    public void setAuthors(final List<SyndPerson> authors) {
        this.authors = authors;
    }

    /**
     * Returns the entry author.
     * <p>
     * 
     * @return the entry author, <b>null</b> if none.
     * 
     */
    public List<SyndPerson> getAuthors() {
        if (authors == null) {
            authors = new ArrayList<SyndPerson>();
        }
        return authors;
    }

    /**
     * Set the categories
     * <p>
     * 
     * @param categories The categories to set.
     * @since Atom 1.0
     */
    public void setCategories(final List<Category> categories) {
        this.categories = categories;
    }

    /**
     * Returns the categories
     * <p>
     * 
     * @return Returns the categories.
     * @since Atom 1.0
     */
    public List<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<Category>();
        }
        return categories;
    }

    /**
     * Sets the entry contents.
     * <p>
     * 
     * @param contents the list of Content elements with the entry contents to
     *            set, an empty list or <b>null</b> if none.
     */
    public void setContents(final List<Content> contents) {
        this.contents = contents;
    }

    /**
     * Returns the entry contents.
     * <p>
     * 
     * @return a list of Content elements with the entry contents, an empty list
     *         if none.
     */
    public List<Content> getContents() {
        if (contents == null) {
            contents = new ArrayList<Content>();
        }
        return contents;
    }

    /**
     * Sets the entry contributors.
     * <p>
     * 
     * @param contributors the list of Person elements with the entry
     *            contributors to set, an empty list or <b>null</b> if none.
     * 
     */
    public void setContributors(final List<SyndPerson> contributors) {
        this.contributors = contributors;
    }

    /**
     * Returns the entry contributors.
     * <p>
     * 
     * @return a list of Person elements with the entry contributors, an empty
     *         list if none.
     * 
     */
    public List<SyndPerson> getContributors() {
        if (contributors == null) {
            contributors = new ArrayList<SyndPerson>();
        }
        return contributors;
    }

    /**
     * Sets the entry created date (Atom 0.3 only)
     * <p>
     * 
     * @param created the entry created date, <b>null</b> if none.
     */
    public void setCreated(final Date created) {
        this.created = new Date(created.getTime());
    }

    /**
     * Returns the entry created date (Atom 0.3 only)
     * <p>
     * 
     * @return the entry created date, <b>null</b> if none.
     */
    public Date getCreated() {
        if (created == null) {
            return null;
        } else {
            return new Date(created.getTime());
        }
    }

    /**
     * Sets foreign markup found at entry level.
     * <p>
     * 
     * @param foreignMarkup Opaque object to discourage use
     * 
     */
    public void setForeignMarkup(final List<Element> foreignMarkup) {
        this.foreignMarkup = foreignMarkup;
    }

    /**
     * Returns foreign markup found at entry level.
     * <p>
     * 
     * @return list of Opaque object to discourage use
     * 
     */
    public List<Element> getForeignMarkup() {
        if (foreignMarkup == null) {
            foreignMarkup = new ArrayList<Element>();
        }
        return foreignMarkup;
    }

    /**
     * Sets the entry ID.
     * <p>
     * 
     * @param id the entry ID, <b>null</b> if none.
     * 
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Returns the entry ID.
     * <p>
     * 
     * @return the entry ID, <b>null</b> if none.
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the entry issued date (Atom 0.3, maps to
     * {@link #setPublished(java.util.Date)}).
     * <p>
     * 
     * @param issued the entry issued date, <b>null</b> if none.
     */
    public void setIssued(final Date issued) {
        if (issued == null) {
            published = null;
        } else {
            published = new Date(issued.getTime());
        }
    }

    /**
     * Returns the entry issued date (Atom 0.3, maps to {@link #getPublished()}
     * ).
     * <p>
     * 
     * @return the entry issued date, <b>null</b> if none.
     */
    public Date getIssued() {
        if (published == null) {
            return null;
        } else {
            return new Date(published.getTime());
        }
    }

    /**
     * Returns true if entry is a media entry, i.e. has rel="edit-media".
     * 
     * @return true if entry is a media entry
     */
    public boolean isMediaEntry() {
        boolean mediaEntry = false;
        final List<Link> links = getOtherLinks();

        for (final Link link : links) {
            if ("edit-media".equals(link.getRel())) {
                mediaEntry = true;

                break;
            }
        }

        return mediaEntry;
    }

    /**
     * Sets the entry modified date (Atom 0.3, maps to
     * {@link #setUpdated(java.util.Date)}).
     * <p>
     * 
     * @param modified the entry modified date, <b>null</b> if none.
     */
    public void setModified(final Date modified) {
        if (modified == null) {
            updated = null;
        } else {
            updated = new Date(modified.getTime());
        }
    }

    /**
     * Returns the entry modified date (Atom 0.3, maps to {@link #getUpdated()}
     * ).
     * <p>
     * 
     * @return the entry modified date, <b>null</b> if none.
     */
    public Date getModified() {
        if (updated == null) {
            return null;
        } else {
            return new Date(updated.getTime());
        }
    }

    /**
     * Returns the module identified by a given URI.
     * <p>
     * 
     * @param uri the URI of the ModuleImpl.
     * @return The module with the given URI, <b>null</b> if none.
     */
    @Override
    public Module getModule(final String uri) {
        return ModuleUtils.getModule(modules, uri);
    }

    /**
     * Sets the entry modules.
     * <p>
     * 
     * @param modules the list of ModuleImpl elements with the entry modules to
     *            set, an empty list or <b>null</b> if none.
     * 
     */
    @Override
    public void setModules(final List<Module> modules) {
        this.modules = modules;
    }

    /**
     * Returns the entry modules.
     * <p>
     * 
     * @return a list of ModuleImpl elements with the entry modules, an emtpy
     *         list if none.
     * 
     */
    @Override
    public List<Module> getModules() {
        if (modules == null) {
            modules = new ArrayList<Module>();
        }
        return modules;
    }

    /**
     * Sets the entry non-alternate links.
     * <p>
     * 
     * @param otherLinks the list Link elements with the entry non-alternate
     *            links to set, an empty list or <b>null</b> if none.
     */
    public void setOtherLinks(final List<Link> otherLinks) {
        this.otherLinks = otherLinks;
    }

    /**
     * Returns the entry non-alternate links.
     * <p>
     * 
     * @return the list of Link elements with the entry non-alternate links to
     *         set, an empty list if none.
     */
    public List<Link> getOtherLinks() {
        if (otherLinks == null) {
            otherLinks = new ArrayList<Link>();
        }
        return otherLinks;
    }

    /**
     * Set the published
     * <p>
     * 
     * @param published The published to set.
     * @since Atom 1.0
     */
    public void setPublished(final Date published) {
        if (published == null) {
            this.published = null;
        } else {
            this.published = new Date(published.getTime());
        }
    }

    /**
     * Returns the published
     * <p>
     * 
     * @return Returns the published.
     * @since Atom 1.0
     */
    public Date getPublished() {
        if (published == null) {
            return null;
        } else {
            return new Date(published.getTime());
        }
    }

    /**
     * Set the rights
     * <p>
     * 
     * @param rights The rights to set.
     * @since Atom 1.0
     */
    public void setRights(final String rights) {
        this.rights = rights;
    }

    /**
     * Returns the rights
     * <p>
     * 
     * @return Returns the rights.
     * @since Atom 1.0
     */
    public String getRights() {
        return rights;
    }

    /**
     * Set the source
     * <p>
     * 
     * @param source The source to set.
     */
    public void setSource(final Feed source) {
        this.source = source;
    }

    /**
     * Returns the source
     * <p>
     * 
     * @return Returns the source.
     */
    public Feed getSource() {
        return source;
    }

    /**
     * Sets the entry summary.
     * <p>
     * 
     * @param summary the entry summary, <b>null</b> if none.
     * 
     */
    public void setSummary(final Content summary) {
        this.summary = summary;
    }

    /**
     * Returns the entry summary.
     * <p>
     * 
     * @return the entry summary, <b>null</b> if none.
     * 
     */
    public Content getSummary() {
        return summary;
    }

    /**
     * Sets the entry title.
     * <p>
     * 
     * @param title the entry title, <b>null</b> if none.
     * 
     */
    public void setTitle(final String title) {
        if (this.title == null) {
            this.title = new Content();
        }

        this.title.setValue(title);
    }

    /**
     * Returns the entry title.
     * <p>
     * 
     * @return the entry title, <b>null</b> if none.
     * 
     */
    public String getTitle() {
        if (title != null) {
            return title.getValue();
        }

        return null;
    }

    /**
     * Sets the entry title as a text construct.
     * <p>
     * 
     * @param title the entry title, <b>null</b> if none.
     * 
     */
    public void setTitleEx(final Content title) {
        this.title = title;
    }

    /**
     * Returns the entry title as a text construct.
     * <p>
     * 
     * @return the entry title, <b>null</b> if none.
     * 
     */
    public Content getTitleEx() {
        return title;
    }

    /**
     * Set the updated
     * <p>
     * 
     * @param updated The updated to set.
     * @since Atom 1.0
     */
    public void setUpdated(final Date updated) {
        if (updated == null) {
            this.updated = null;
        } else {
            this.updated = new Date(updated.getTime());
        }
    }

    /**
     * Returns the updated
     * <p>
     * 
     * @return Returns the updated.
     * @since Atom 1.0
     */
    public Date getUpdated() {
        if (updated == null) {
            return null;
        } else {
            return new Date(updated.getTime());
        }
    }

    /**
     * Set the xmlBase
     * <p>
     * 
     * @param xmlBase The xmlBase to set.
     * @since Atom 1.0
     */
    public void setXmlBase(final String xmlBase) {
        this.xmlBase = xmlBase;
    }

    /**
     * Returns the xmlBase
     * <p>
     * 
     * @return Returns the xmlBase.
     * @since Atom 1.0
     */
    public String getXmlBase() {
        return xmlBase;
    }

    /**
     * Creates a deep 'bean' clone of the object.
     * <p>
     * 
     * @return a clone of the object.
     * @throws CloneNotSupportedException thrown if an element of the object
     *             cannot be cloned.
     * 
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return objBean.clone();
    }

    /**
     * Indicates whether some other object is "equal to" this one as defined by
     * the Object equals() method.
     * <p>
     * 
     * @param other he reference object with which to compare.
     * @return <b>true</b> if 'this' object is equal to the 'other' object.
     * 
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Entry)) {
            return false;
        }
        // can't use foreign markup in equals, due to JDOM equals impl
        final List<Element> fm = getForeignMarkup();
        setForeignMarkup(((Entry) other).getForeignMarkup());

        final boolean ret = objBean.equals(other);
        // restore foreign markup
        setForeignMarkup(fm);

        return ret;
    }

    /**
     * Returns a hashcode value for the object.
     * <p>
     * It follows the contract defined by the Object hashCode() method.
     * <p>
     * 
     * @return the hashcode of the bean object.
     * 
     */
    @Override
    public int hashCode() {
        return objBean.hashCode();
    }

    /**
     * Returns the String representation for the object.
     * <p>
     * 
     * @return String representation for the object.
     * 
     */
    @Override
    public String toString() {
        return objBean.toString();
    }

    public Link findRelatedLink(final String relation) {
        for (final Link l : otherLinks) {
            if (relation.equals(l.getRel())) {
                return l;
            }
        }
        return null;
    }
}