package org.imixs.workflow.faces.util;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

/**
 * für ConfigItem benutzter Converter, der einen Komma-separierten String in
 * einen Vektor umwandelt und umgekehrt.
 * 
 * Noch dringend zu tun: - Dem Converter im Fehlerfall noch eine eigene
 * Fehlermeldung mitgeben - müssen da nicht noch eine Menge try-catch blöcke und
 * Typ-Prüfungen rein? Derzeit geht das alles sehr optimistisch davon aus, dass
 * in dem Vektor wirklich auch Strings drin sind; was eigentlich auch der Fall
 * ist. Interessant wird es, wenn man bestehende Felder umbiegt.
 * 
 * Schön wäre noch folgendes: - Den Separator im converter-tag der JSP Seite
 * definieren. Das wird allerdings ein Act (Vorgehen beschrieben in Kap. 20.4 in
 * "Kito Mann - JSF in Action")
 */
@SuppressWarnings(value = { "rawtypes" }) @FacesConverter(value = "org.imixs.VectorConverter") public class VectorConverter implements Converter {
  private String separator = "\n";

  @SuppressWarnings(value = { "unchecked" }) public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
    Vector v = new Vector();
    String[] tokens = value.split(separator);
    for (int i = 0; i < tokens.length; i++) {
      v.addElement(tokens[i].trim());
    }
    return v;
  }

  public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException {
    String result = "";
    if (value instanceof List) {
      ListIterator interator = ((List) value).listIterator();
      while (interator.hasNext()) {
        result = result + interator.next();
        if (interator.hasNext()) {
          result = result + separator;
        }
      }
    }
    return result;
  }
}