package com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 Sven Jacobs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Date;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.svenjacobs.gwtbootstrap3.client.shared.event.HideEvent;
import com.svenjacobs.gwtbootstrap3.client.shared.event.HideHandler;
import com.svenjacobs.gwtbootstrap3.client.shared.event.ShowEvent;
import com.svenjacobs.gwtbootstrap3.client.shared.event.ShowHandler;
import com.svenjacobs.gwtbootstrap3.client.ui.HasId;
import com.svenjacobs.gwtbootstrap3.client.ui.HasPlaceholder;
import com.svenjacobs.gwtbootstrap3.client.ui.HasResponsiveness;
import com.svenjacobs.gwtbootstrap3.client.ui.Icon;
import com.svenjacobs.gwtbootstrap3.client.ui.TextBox;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasDateIcon;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasDownIcon;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasEndDate;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasFormat;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasShowDatePicker;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasShowTimePicker;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasStartDate;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasStrict;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasTimeIcon;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasUpIcon;
import com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.constant.HasVisibleHandlers;

/**
 * @author Joshua Godi
 */
public class DateTimeBoxBase extends Widget implements HasValue<Date>, HasEnabled, HasValueChangeHandlers<Date>,
        HasVisibility, HasChangeHandlers, HasVisibleHandlers, HasId, HasResponsiveness, HasDateIcon, HasShowDatePicker,
        HasShowTimePicker, HasDownIcon, HasEndDate, HasStartDate, HasStrict, HasTimeIcon, HasUpIcon, HasFormat,
        HasPlaceholder {

    /** Moment.js date format */
    private static final String DEFAULT_FORMAT = "YYYY-MM-DD HH:mm";

    private final TextBox textBox;
    private String format;

    private boolean showTime = true;
    private boolean showDate = true;
    private boolean useStrict = false;
    private String dateIconClass = "fa fa-calendar";
    private String timeIconClass = "fa fa-clock-o";
    private String upIconClass = "fa fa-arrow-up";
    private String downIconClass = "fa fa-arrow-down";

    public DateTimeBoxBase() {
        textBox = new TextBox();
        setElement(textBox.getElement());
        setFormat(DEFAULT_FORMAT);
        setValue(new Date());
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public void setAlignment(final ValueBoxBase.TextAlignment align) {
        textBox.setAlignment(align);
    }

<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    @Override
    public void setPlaceholder(final String placeHolder) {
        textBox.setPlaceholder(placeHolder);
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setPlaceHolder(String placeHolder) {
        textBox.setPlaceHolder(placeHolder);
=======
    public void setPlaceHolder(final String placeHolder) {
        textBox.setPlaceHolder(placeHolder);
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    @Override
    public String getPlaceholder() {
        return textBox.getPlaceholder();
    }

    public void setReadOnly(final boolean readOnly) {
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setReadOnly(boolean readOnly) {
=======
    public void setReadOnly(final boolean readOnly) {
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
        textBox.setReadOnly(readOnly);
    }

    public boolean isReadOnly() {
        return textBox.isReadOnly();
    }

    public void reload() {
        configure();
    }

    public void show() {
        show(textBox.getElement());
    }

    public void hide() {
        hide(textBox.getElement());
    }

    @Override
    public void setEndDate(final Date endDate) {
        setEndDate(textBox.getElement(), endDate);
    }

    @Override
    public void setStartDate(final Date startDate) {
        setStartDate(textBox.getElement(), startDate);
    }

    @Override
    public void setUseStrict(final boolean useStrict) {
        this.useStrict = useStrict;
    }

    @Override
<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    public void setDateIcon(final Icon icon) {
        dateIconClass = icon.getStyleName();
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setDateIcon(Icon icon) {
        this.dateIconClass = icon.getStyleName();
=======
    public void setDateIcon(final Icon icon) {
        this.dateIconClass = icon.getStyleName();
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

    @Override
<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    public void setDownIcon(final Icon icon) {
        downIconClass = icon.getStyleName();
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setDownIcon(Icon icon) {
        this.downIconClass = icon.getStyleName();
=======
    public void setDownIcon(final Icon icon) {
        this.downIconClass = icon.getStyleName();
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

    @Override
<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    public void setTimeIcon(final Icon icon) {
        timeIconClass = icon.getStyleName();
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setTimeIcon(Icon icon) {
        this.timeIconClass = icon.getStyleName();
=======
    public void setTimeIcon(final Icon icon) {
        this.timeIconClass = icon.getStyleName();
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

    @Override
<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    public void setUpIcon(final Icon icon) {
        upIconClass = icon.getStyleName();
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setUpIcon(Icon icon) {
        this.upIconClass = icon.getStyleName();
=======
    public void setUpIcon(final Icon icon) {
        this.upIconClass = icon.getStyleName();
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

    @Override
<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    public void setShowDatePicker(final boolean showDatePicker) {
        showDate = showDatePicker;
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setShowDatePicker(boolean showDatePicker) {
        this.showDate = showDatePicker;
=======
    public void setShowDatePicker(final boolean showDatePicker) {
        this.showDate = showDatePicker;
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

    @Override
<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    public void setShowTimePicker(final boolean showTimePicker) {
        showTime = showTimePicker;
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    public void setShowTimePicker(boolean showTimePicker) {
        this.showTime = showTimePicker;
=======
    public void setShowTimePicker(final boolean showTimePicker) {
        this.showTime = showTimePicker;
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

    @Override
    public void setFormat(final String format) {
        this.format = format;
        // dateTimeFormat = DateTimeFormat.getFormat(format);

        final Date oldValue = getValue();
        if (oldValue != null) {
            setValue(oldValue);
        }
    }

    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        return addHandler(handler, ChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addHideHandler(final HideHandler handler) {
        return addHandler(handler, HideEvent.getType());
    }

    @Override
    public HandlerRegistration addShowHandler(final ShowHandler handler) {
        return addHandler(handler, ShowEvent.getType());
    }

    @Override
    public boolean isEnabled() {
        return textBox.isEnabled();
    }

    @Override
    public void setEnabled(final boolean enabled) {
        textBox.setEnabled(enabled);
    }

    @Override
    public void setId(final String id) {
        textBox.setId(id);
    }

    @Override
    public String getId() {
        return textBox.getId();
    }

    @Override
    public void setVisibleOn(final String deviceSizeString) {
        textBox.setVisibleOn(deviceSizeString);
    }

    @Override
    public void setHiddenOn(final String deviceSizeString) {
        textBox.setHiddenOn(deviceSizeString);
    }

    @Override
    public Date getValue() {
        final String value = textBox.getValue();
        if (!(value == null || "".equals(value))) {
            return null;
        }
        try {
            final JsDate date = parse(textBox.getValue(), format);
            return new Date((long) date.getTime());
        } catch (final Exception e) {
            GWT.log("JS error", e);
        }
        return null;
    }

    @Override
    public void setValue(final Date value) {
        setValue(value, false);
    }

    @Override
    public void setValue(final Date value, final boolean fireEvents) {
        // We defer the command so that the box is setup properly
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                updateValue(textBox.getElement(), value);

                if (fireEvents) {
                    ValueChangeEvent.fire(DateTimeBoxBase.this, value);
                }
            }
        });
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Date> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLoad() {
        super.onLoad();
        configure();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        execute("remove");
    }

    public void onChange() {
        ValueChangeEvent.fire(this, getValue());
    }

    public void onShow(final Event e) {
        fireEvent(new ShowEvent(e));
    }

    public void onHide(final Event e) {
        fireEvent(new HideEvent(e));
    }

    protected void configure() {
        configure(this);
    }

<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/left.java
    protected void configure(final Widget w) {
        w.getElement().setAttribute("data-format", format);
        configure(w.getElement(), showTime, showDate, useStrict, timeIconClass, dateIconClass, upIconClass,
                downIconClass);
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/base.java
    protected void configure(Widget w) {
        w.getElement().setAttribute("data-date-format", format);
        configure(w.getElement(), showTime, showDate, useStrict, timeIconClass, dateIconClass, upIconClass, downIconClass);
=======
    protected void configure(final Widget w) {
        w.getElement().setAttribute("data-date-format", format);
        configure(w.getElement(), showTime, showDate, useStrict, timeIconClass, dateIconClass, upIconClass, downIconClass);
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/4fc9137691f262d79c1f7844d85659dea906122c/gwtbootstrap3/src/main/java/com/svenjacobs/gwtbootstrap3/datetimepicker/client/ui/base/DateTimeBoxBase.java/right.java
    }

    protected void execute(final String cmd) {
        execute(getElement(), cmd);
    }

    // @formatter:off
    
    protected native JsDate parse(final String dateStr, final String format) /*-{
        return $wnd.moment(dateStr, format).toDate();
    }-*/;
    
    protected native String format(final Date date, final String format) /*-{
        return $wnd.moment(date).format(format);
    }-*/;
    
    protected native void updateValue(Element e, Date newDate) /*-{
        if ($wnd.jQuery(e).data('DateTimePicker')) {
            $wnd.jQuery(e).data('DateTimePicker').setDate(newDate);
        }
    }-*/;

    protected native void show(Element e) /*-{
        if ($wnd.jQuery(e).data('DateTimePicker')) {
            $wnd.jQuery(e).data('DateTimePicker').show();
        }
    }-*/;

    protected native void hide(Element e) /*-{
        if ($wnd.jQuery(e).data('DateTimePicker')) {
            $wnd.jQuery(e).data('DateTimePicker').hide();
        }
    }-*/;

    protected native void setStartDate(Element e, Date startDate) /*-{
        if ($wnd.jQuery(e).data('DateTimePicker')) {
            $wnd.jQuery(e).data('DateTimePicker').setStartDate(startDate);
        }
    }-*/;

    protected native void setEndDate(Element e, Date endDate) /*-{
        if ($wnd.jQuery(e).data('DateTimePicker')) {
            $wnd.jQuery(e).data('DateTimePicker').setEndDate(endDate);
        }
    }-*/;

    private native void execute(Element e, String cmd) /*-{
        $wnd.jQuery(e).datetimepicker(cmd);
    }-*/;

    protected native void configure(Element e, boolean showTime, boolean showDate, boolean useStrict,
            String timeIconClass, String dateIconClass, String upIconClass, String downIconClass) /*-{
        var that = this;
        $wnd.jQuery(e).datetimepicker({
            pickDate: showDate,
            pickTime: showTime,
            useStrict: useStrict,
            icons: {
                time: timeIconClass,
                date: dateIconClass,
                up: upIconClass,
                down: downIconClass
            }
        })
        .on('change.dp', function () {
            that.@com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.DateTimeBoxBase::onChange()();
        })
        .on("show.dp", function (e) {
            that.@com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.DateTimeBoxBase::onShow(Lcom/google/gwt/user/client/Event;)(e);
        })
        .on("hide.dp", function (e) {
            that.@com.svenjacobs.gwtbootstrap3.datetimepicker.client.ui.base.DateTimeBoxBase::onHide(Lcom/google/gwt/user/client/Event;)(e);
        });
    }-*/;
}
