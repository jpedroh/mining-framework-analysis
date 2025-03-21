package com.googlecode.gwtphonegap.client.notification;
import com.google.gwt.user.client.Window;

public class NotificationBrowserImpl implements Notification {
  @Override public void alert(String message) {
    Window.alert(message);
  }

  @Override public void beep(int count) {
  }

  @Override public void vibrate(int milliseconds) {
  }

  @Override public void vibrateWithPattern(int[] pattern) {
  }

  @Override public void vibrateWithPattern(int[] pattern, int repeat) {
  }

  @Override public void cancelVibrate() {
  }

  @Override public void prompt(String message, PromptCallback callback) {
    String enteredValue = Window.prompt(message, "OK");
    PromptResults results;
    if (enteredValue == null) {
      results = new PromptResultsBrowserImpl(1, null);
    } else {
      results = new PromptResultsBrowserImpl(0, enteredValue);
    }
    callback.onPrompt(results);
  }

  @Override public void prompt(String message, PromptCallback callback, String title) {
    prompt(message, callback);
  }

  @Override public void prompt(String message, PromptCallback callback, String title, String defaultText) {
    prompt(message, callback);
  }

  @Override public void prompt(String message, PromptCallback callback, String title, String defaultText, String[] buttonLabels) {
    prompt(message, callback);
  }

  @Override public void alert(String message, AlertCallback callback) {
    Window.alert(message);
    callback.onOkButtonClicked();
  }

  @Override public void alert(String message, AlertCallback callback, String title) {
    alert(message, callback);
  }

  @Override public void alert(String message, AlertCallback callback, String title, String buttonName) {
    alert(message, callback);
  }

  @Override public void confirm(String message, ConfirmCallback callback) {
    boolean confirm = Window.confirm(message);
    callback.onConfirm(confirm ? 1 : 2);
  }

  @Override public void confirm(String message, ConfirmCallback callback, String title) {
    confirm(message, callback);
  }

  @Override public void confirm(String message, ConfirmCallback callback, String title, String[] buttonLabels) {
    confirm(message, callback);
  }
}