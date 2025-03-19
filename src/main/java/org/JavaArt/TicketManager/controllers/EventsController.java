package org.JavaArt.TicketManager.controllers;
import org.JavaArt.TicketManager.entities.Event;
import org.JavaArt.TicketManager.entities.Sector;
import org.JavaArt.TicketManager.entities.SectorDefaults;
import org.JavaArt.TicketManager.service.EventService;
import org.JavaArt.TicketManager.service.SectorDefaultsService;
import org.JavaArt.TicketManager.service.SectorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller @SessionAttributes(value = { "pageName", "eventErrorMessage", "errorMessageEdit", "events", "event", "sector", "sectors", "allSectors", "eventEditHidden", "eventTime", "eventDescriptions", "eventBookingTimeOut", "dateEvent", "sectorName", "maxRows", "maxSeats", "newPrice" }) public class EventsController {
  public Event editEvent;

  private EventService eventService = EventService.getInstance();

  private SectorService sectorService = new SectorService();

  private SectorDefaultsService sectorDefaultsService = new SectorDefaultsService();

  @RequestMapping(value = "Events/Events.do", method = RequestMethod.GET) public String eventGet(Model model) {
    model.addAttribute("pageName", 4);
    List<Event> events = eventService.getAllEvents();
    if (events != null && events.size() > 0) {
      Collections.sort(events);
    }
    if (events != null && events.size() > 0) {
      model.addAttribute("event", events.get(0));
      model.addAttribute("events", events);
    }
    return "Events";
  }

  @RequestMapping(value = "AddEditEvent/NewEvent.do", method = RequestMethod.GET) public String newEventGet(Model model, SessionStatus status) throws ParseException {
    model.addAttribute("pageName", 4);
    String eventErrorMessage = (String) model.asMap().get("eventErrorMessage");
    String errorMessageEdit = (String) model.asMap().get("errorMessageEdit");
    if ((errorMessageEdit != null) || (eventErrorMessage != null)) {
      model.addAttribute("errorMessageEdit", "");
      model.addAttribute("eventErrorMessage", "");
    }
    model.addAttribute("eventDescriptions", "");
    model.addAttribute("eventBookingTimeOut", 0);
    List<SectorDefaults> sectorsDefaults = sectorDefaultsService.getAllSectorDefaults();
    if (sectorsDefaults != null && sectorsDefaults.size() != 0) {
      Collections.sort(sectorsDefaults);
    }
    Map<String, Sector> allSectors = new TreeMap<>();
    if (sectorsDefaults != null && sectorsDefaults.size() != 0) {
      allSectors.clear();
      List copy = new ArrayList(sectorsDefaults);
      for (Iterator<SectorDefaults> it = copy.iterator(); it.hasNext(); ) {
        SectorDefaults sectorDefaults = it.next();
        Sector sector = new Sector();
        sector.setName(sectorDefaults.getSectorName());
        sector.setMaxRows(sectorDefaults.getMaxRows());
        sector.setMaxSeats(sectorDefaults.getMaxSeats());
        sector.setPrice(sectorDefaults.getDefaultPrice());
        sectorService.addSector(sector);
        model.addAttribute("price" + sector.getId(), sector.getId());
        allSectors.put(sector.getName(), sector);
      }
    }
    if (allSectors != null && allSectors.size() > 0) {
      model.addAttribute("allSectors", allSectors);
    }
    Date today = new Date();
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime(today);
    int year = gc.get(GregorianCalendar.YEAR);
    int mon = gc.get(GregorianCalendar.MONTH);
    int day = gc.get(GregorianCalendar.DATE);
    GregorianCalendar calendarN = new GregorianCalendar();
    calendarN.set(year, mon, day + 1, 0, 0, 0);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String localisedDate = dateFormat.format(calendarN.getTime());
    Date dateEvent = dateFormat.parse(localisedDate);
    String eventTime = "12:00";
    model.addAttribute("eventTime", eventTime);
    model.addAttribute("dateEvent", dateEvent);
    return "AddEditEvent";
  }

  @RequestMapping(value = "Events/setDelete.do", method = RequestMethod.POST) public String eventsSetDelete(@RequestParam(value = "eventId", required = true) Long eventId, SessionStatus status, Model model) {
    Event event = eventService.getEventById(eventId);
    if (eventService.busyEvent(event) == false) {
      eventService.deleteEvent(event);
      status.setComplete();
      return "redirect:/Events/Events.do";
    } else {
      String eventsErrorMessage = "\u042d\u0442\u043e \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435 \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043d\u0435\u043b\u044c\u0437\u044f, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0430 \u043d\u0435\u0433\u043e \u0443\u0436\u0435 \u043a\u0443\u043f\u043b\u0435\u043d\u044b \u0431\u0438\u043b\u0435\u0442\u044b!";
      model.addAttribute("eventsErrorMessage", eventsErrorMessage);
      return "Events";
    }
  }

  @RequestMapping(value = "Events/Redirect.do", method = RequestMethod.POST) public String eventsRedirect() {
    return "redirect:/AddEditEvent/NewEvent.do";
  }

  @RequestMapping(value = "AddEditEvent/addEvent.do", method = RequestMethod.POST) public String addEvent(Model model, SessionStatus status, HttpServletRequest request) throws SQLException, ParseException {
    List<Event> events = eventService.getAllEvents();
    String eventErrorMessage = null;
    Map allSectors = (TreeMap) model.asMap().get("allSectors");
    String dateEvent = request.getParameter("dateEvent");
    String eventTime = request.getParameter("eventTime");
    String eventDescriptions = request.getParameter("eventDescriptions");
    String eventBookingTimeOutS = request.getParameter("eventBookingTimeOut");
    int eventBookingTimeOut = 0;
    if (eventBookingTimeOutS != null) {
      eventBookingTimeOut = Integer.parseInt(eventBookingTimeOutS);
    }
    String action = request.getParameter("delete");
    Long idSectorDel;
    if (action != null) {
      idSectorDel = Long.parseLong(action);
      Sector sector = sectorService.getSectorById(idSectorDel);
      if (sectorService.busySector(sector) == false) {
        sectorService.deleteSector(sector);
        allSectors.values().remove(sector);
        if (allSectors != null) {
          model.addAttribute("allSectors", allSectors);
        }
        model.addAttribute("eventDescriptions", eventDescriptions);
        model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
        Date simpleDate = (Date) getDateByString(dateEvent, eventTime).get(0);
        model.addAttribute("dateEvent", simpleDate);
        int hour = (int) getDateByString(dateEvent, eventTime).get(1);
        int min = (int) getDateByString(dateEvent, eventTime).get(2);
        String timeEvent;
        if (min == 0) {
          timeEvent = "" + hour + ":" + "00";
        } else {
          timeEvent = "" + hour + ":" + min;
        }
        model.addAttribute("eventTime", timeEvent);
        return "AddEditEvent";
      } else {
        String sectorErrorMessage = "\u042d\u0442\u043e\u0442 \u0441\u0435\u043a\u0442\u043e\u0440 \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043d\u0435\u043b\u044c\u0437\u044f, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0430 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435 \u0443\u0436\u0435 \u043a\u0443\u043f\u043b\u0435\u043d\u044b \u0431\u0438\u043b\u0435\u0442\u044b!";
        model.addAttribute("sectorErrorMessage", sectorErrorMessage);
        if (allSectors != null) {
          model.addAttribute("allSectors", allSectors);
        }
        model.addAttribute("eventDescriptions", eventDescriptions);
        model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
        Date simpleDate = (Date) getDateByString(dateEvent, eventTime).get(0);
        model.addAttribute("dateEvent", simpleDate);
        int hour = (int) getDateByString(dateEvent, eventTime).get(1);
        int min = (int) getDateByString(dateEvent, eventTime).get(2);
        String timeEvent;
        if (min == 0) {
          timeEvent = "" + hour + ":" + "00";
        } else {
          timeEvent = "" + hour + ":" + min;
        }
        model.addAttribute("eventTime", timeEvent);
        return "AddEditEvent";
      }
    }
    String action1 = request.getParameter("action");
    if (action1.equals("save")) {
      if (!eventDescriptions.equals("".trim()) && !dateEvent.equals("") && (dateValid(dateEvent) != false) && !eventTime.equals("") && eventBookingTimeOut != 0) {
        Event event = new Event();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date trueDate = format.parse(dateEvent);
        int intHour = 0;
        int intMin = 0;
        if ((eventTime != null) && (eventTime != "")) {
          String[] str = eventTime.split(":");
          intHour = Integer.parseInt(str[0]);
          intMin = Integer.parseInt(str[1]);
        }
        Calendar rightAgain = Calendar.getInstance();
        rightAgain.setTime(trueDate);
        rightAgain.add(Calendar.HOUR, intHour);
        rightAgain.add(Calendar.MINUTE, intMin);
        Date trueDateNew = rightAgain.getTime();
        List<Event> list = eventService.getEventsByDate(trueDateNew);
        if ((list == null) || (list.size() == 0)) {
          event.setDate(trueDateNew);
          boolean isDeleted = false;
          event.setDeleted(isDeleted);
          event.setDescription("" + eventDescriptions.trim());
          Date nowDate = new Date();
          event.setTimeStamp(nowDate);
          event.setBookingTimeOut(new Date(event.getDate().getTime() - eventBookingTimeOut * 60000));
          eventService.addEvent(event);
          events.add(event);
          Iterator<Sector> sectorNewList = null;
          if (allSectors != null) {
            sectorNewList = allSectors.values().iterator();
          } else {
            allSectors = new TreeMap<>();
            sectorNewList = allSectors.values().iterator();
          }
          while (sectorNewList.hasNext()) {
            Sector sectorNew = sectorNewList.next();
            String price = request.getParameter("price" + sectorNew.getId());
            try {
              Double priceNew = Double.parseDouble(price);
              sectorNew.setPrice(priceNew);
              sectorNew.setEvent(event);
            } catch (Exception e) {
            }
            sectorService.addSector(sectorNew);
          }
          event.setDate(trueDate);
        } else {
          eventErrorMessage = " \u041c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435 \u043d\u0430 \u044d\u0442\u0443 \u0434\u0430\u0442\u0443 \u0443\u0436\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!" + "<br>";
        }
      } else {
        if (eventErrorMessage == null) {
          eventErrorMessage = "" + "<br>";
        }
        if (eventDescriptions.equals("")) {
          eventErrorMessage += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u043d\u0430\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u043d\u0438\u0435 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
        if (dateValid(dateEvent) == false) {
          eventErrorMessage += " \u041d\u0435\u043a\u043e\u0440\u0440\u0435\u043a\u0442\u043d\u043e \u0437\u0430\u043f\u043e\u043b\u043d\u0435\u043d\u043d\u0430\u044f \u0434\u0430\u0442\u0430 - \u0434\u0430\u0442\u0430 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0444\u043e\u0440\u043c\u0430\u0442\u0435 \'\u0434\u0434.\u043c\u043c.\u0433\u0433\u0433\u0433\' \u0438 \u0431\u043e\u043b\u044c\u0448\u0435 \u0442\u0435\u043a\u0443\u0449\u0435\u0439!" + "<br>";
        }
        if (dateEvent.equals("")) {
          eventErrorMessage += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u0434\u0435\u043d\u044c \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
        if (eventTime.equals("")) {
          eventErrorMessage += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u0432\u0440\u0435\u043c\u044f \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
        if (eventBookingTimeOut == 0) {
          eventErrorMessage += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u0432\u0440\u0435\u043c\u044f \u0443\u0434\u0430\u043b\u0435\u043d\u0438\u044f \u0431\u0440\u043e\u043d\u0438 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
      }
    }
    if (eventErrorMessage != null && !eventErrorMessage.equals("")) {
      model.addAttribute("eventErrorMessage", eventErrorMessage);
      model.addAttribute("eventDescriptions", eventDescriptions);
      model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
      Iterator<Sector> sectorNewList = null;
      if (allSectors != null) {
        sectorNewList = allSectors.values().iterator();
      } else {
        allSectors = new TreeMap<>();
        sectorNewList = allSectors.values().iterator();
      }
      while (sectorNewList.hasNext()) {
        Sector sectorNew = sectorNewList.next();
        String price = request.getParameter("price" + sectorNew.getId());
        try {
          Double priceNew = Double.parseDouble(price);
          sectorNew.setPrice(priceNew);
          model.addAttribute("id" + sectorNew.getId(), sectorNew.getId());
          allSectors.put(sectorNew.getName(), sectorNew);
        } catch (Exception e) {
        }
      }
      model.addAttribute("allSectors", allSectors);
      Date simpleDate = (Date) getDateByString(dateEvent, eventTime).get(0);
      model.addAttribute("dateEvent", simpleDate);
      int hour = (int) getDateByString(dateEvent, eventTime).get(1);
      int min = (int) getDateByString(dateEvent, eventTime).get(2);
      String timeEvent = "";
      if (min == 0) {
        timeEvent = "" + hour + ":" + "00";
      } else {
        timeEvent = "" + hour + ":" + min;
      }
      model.addAttribute("eventTime", timeEvent);
      return "AddEditEvent";
    }
    status.setComplete();
    return "redirect:/Events/Events.do";
  }

  @RequestMapping(value = "Events/Edit.do", method = RequestMethod.POST) public String eventsEdit(@RequestParam(value = "evnt") Long evnt) {
    this.editEvent = eventService.getEventById(evnt);
    return "redirect:/AddEditEvent/EditEvent.do";
  }

  @RequestMapping(value = "AddEditEvent/EditEvent.do", method = RequestMethod.GET) public String editEventGet(Model model) throws SQLException, ParseException {
    model.addAttribute("pageName", 4);
    model.addAttribute("eventEdit", editEvent);
    String eventErrorMessage = (String) model.asMap().get("eventErrorMessage");
    String errorMessageEdit = (String) model.asMap().get("errorMessageEdit");
    if ((errorMessageEdit != null) || (eventErrorMessage != null)) {
      model.addAttribute("errorMessageEdit", "");
      model.addAttribute("eventErrorMessage", "");
    }
    Map<String, Sector> allSectors = new TreeMap<>();
    List<Sector> sectors = sectorService.getSectorsByEvent(editEvent);
    if (sectors.size() != 0) {
      List copy = new ArrayList(sectors);
      for (Iterator<Sector> it = copy.iterator(); it.hasNext(); ) {
        Sector sector = it.next();
        model.addAttribute("id" + sector.getId(), sector.getId());
        allSectors.put(sector.getName(), sector);
      }
    }
    model.addAttribute("allSectors", allSectors);
    model.addAttribute("eventDescriptions", (editEvent.getDescription()).trim());
    Date fullEventBookingTimeOut = editEvent.getBookingTimeOut();
    int eventBookingTimeOut = (int) (editEvent.getDate().getTime() - fullEventBookingTimeOut.getTime()) / 60000;
    model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
    Date date = editEvent.getDate();
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime(date);
    int year = gc.get(GregorianCalendar.YEAR);
    int mon = gc.get(GregorianCalendar.MONTH);
    int day = gc.get(GregorianCalendar.DATE);
    int hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
    int min = gc.get(GregorianCalendar.MINUTE);
    GregorianCalendar calendarN = new GregorianCalendar();
    calendarN.set(year, mon, day, 0, 0, 0);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String localisedDate = dateFormat.format(calendarN.getTime());
    Date trueDate = dateFormat.parse(localisedDate);
    model.addAttribute("dateEvent", trueDate);
    String timeEvent;
    if (min == 0) {
      timeEvent = "" + hour + ":" + "00";
    } else {
      timeEvent = "" + hour + ":" + min;
    }
    model.addAttribute("eventTime", timeEvent);
    return "AddEditEvent";
  }

  @RequestMapping(value = "AddEditEvent/editEventNow.do", method = RequestMethod.POST) public String editEvent(Model model, Long eventEditHidden, SessionStatus status, HttpServletRequest request) throws SQLException, ParseException {
    String errorMessageEdit = "";
    Map allSectors = (TreeMap) model.asMap().get("allSectors");
    String dateEventN = request.getParameter("dateEvent");
    String eventTimeN = request.getParameter("eventTime");
    String eventDescriptionsN = request.getParameter("eventDescriptions");
    String eventBookingTimeOutN = request.getParameter("eventBookingTimeOut");
    String action = request.getParameter("delete");
    Long idSectorDel;
    if (action != null) {
      dateEventN = request.getParameter("dateEvent");
      eventTimeN = request.getParameter("eventTime");
      eventDescriptionsN = request.getParameter("eventDescriptions");
      eventBookingTimeOutN = request.getParameter("eventBookingTimeOut");
      idSectorDel = Long.parseLong(action);
      int eventBookingTimeOut = 0;
      if (eventBookingTimeOutN != null) {
        eventBookingTimeOut = Integer.parseInt(eventBookingTimeOutN);
      }
      Sector sector = sectorService.getSectorById(idSectorDel);
      Event event = eventService.getEventById(eventEditHidden);
      if (eventService.busyEvent(event) == false) {
        sectorService.deleteSector(sector);
        allSectors.values().remove(sector);
        model.addAttribute("allSectors", allSectors);
        model.addAttribute("eventDescriptions", eventDescriptionsN);
        model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
        Date simpleDate = (Date) getDateByString(dateEventN, eventTimeN).get(0);
        model.addAttribute("dateEvent", simpleDate);
        int hour = (int) getDateByString(dateEventN, eventTimeN).get(1);
        int min = (int) getDateByString(dateEventN, eventTimeN).get(2);
        String timeEvent;
        if (min == 0) {
          timeEvent = "" + hour + ":" + "00";
        } else {
          timeEvent = "" + hour + ":" + min;
        }
        model.addAttribute("eventEdit", editEvent);
        model.addAttribute("eventTime", timeEvent);
        return "AddEditEvent";
      } else {
        String sectorErrorMessage = "\u042d\u0442\u043e\u0442 \u0441\u0435\u043a\u0442\u043e\u0440 \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u043d\u0435\u043b\u044c\u0437\u044f, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0430 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435 \u0443\u0436\u0435 \u043a\u0443\u043f\u043b\u0435\u043d\u044b \u0431\u0438\u043b\u0435\u0442\u044b!";
        model.addAttribute("sectorErrorMessage", sectorErrorMessage);
        if (allSectors != null) {
          model.addAttribute("allSectors", allSectors);
        }
        model.addAttribute("eventDescriptions", eventDescriptionsN);
        model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
        Date simpleDate = (Date) getDateByString(dateEventN, eventTimeN).get(0);
        model.addAttribute("dateEvent", simpleDate);
        int hour = (int) getDateByString(dateEventN, eventTimeN).get(1);
        int min = (int) getDateByString(dateEventN, eventTimeN).get(2);
        String timeEvent;
        if (min == 0) {
          timeEvent = "" + hour + ":" + "00";
        } else {
          timeEvent = "" + hour + ":" + min;
        }
        model.addAttribute("eventEdit", editEvent);
        model.addAttribute("eventTime", timeEvent);
        return "AddEditEvent";
      }
    }
    String action1 = request.getParameter("action");
    if (action1.equals("save")) {
      dateEventN = request.getParameter("dateEvent");
      eventTimeN = request.getParameter("eventTime");
      eventDescriptionsN = request.getParameter("eventDescriptions");
      eventBookingTimeOutN = request.getParameter("eventBookingTimeOut");
      if (!eventDescriptionsN.equals("") && !dateEventN.equals("") && (dateValid(dateEventN) != false) && !eventTimeN.equals("") && !eventBookingTimeOutN.equals("")) {
        Event event = eventService.getEventById(eventEditHidden);
        Date simpleDate = (Date) getDateByString(dateEventN, eventTimeN).get(0);
        int intHour = 0;
        int intMin = 0;
        if ((eventTimeN != null) && (eventTimeN != "")) {
          String[] str = eventTimeN.split(":");
          intHour = Integer.parseInt(str[0]);
          intMin = Integer.parseInt(str[1]);
        }
        Calendar rightAgain = Calendar.getInstance();
        rightAgain.setTime(simpleDate);
        rightAgain.add(Calendar.HOUR, intHour);
        rightAgain.add(Calendar.MINUTE, intMin);
        Date trueDate = rightAgain.getTime();
        List<Event> list = eventService.getEventsByDateFromEvent(trueDate, event);
        if (list.size() == 0) {
          event.setDate(trueDate);
          boolean isDeleted = false;
          event.setDeleted(isDeleted);
          event.setDescription("" + eventDescriptionsN.trim());
          Date nowDate = new Date();
          event.setTimeStamp(nowDate);
          int time = Integer.parseInt(eventBookingTimeOutN);
          event.setBookingTimeOut(new Date(event.getDate().getTime() - time * 60000));
          if (eventService.busyEvent(event) == false) {
            eventService.updateEvent(event);
            Iterator<Sector> sectorNewList = allSectors.values().iterator();
            while (sectorNewList.hasNext()) {
              Sector sectorNew = sectorNewList.next();
              String price = request.getParameter("price" + sectorNew.getId());
              try {
                Double priceNew = Double.parseDouble(price);
                sectorNew.setPrice(priceNew);
                sectorNew.setEvent(event);
              } catch (Exception e) {
              }
              sectorService.updateSector(sectorNew);
            }
          } else {
            errorMessageEdit = " \u041c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435 c \u043f\u0440\u043e\u0434\u0430\u043d\u043d\u044b\u043c\u0438 \u0431\u0438\u043b\u0435\u0442\u0430\u043c\u0438 \u043d\u0435 \u0440\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u0443\u0435\u0442\u0441\u044f!" + "<br>";
          }
        } else {
          errorMessageEdit = " \u041c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u0435 \u043d\u0430 \u044d\u0442\u0443 \u0434\u0430\u0442\u0443 \u0443\u0436\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442!" + "<br>";
        }
      } else {
        if (errorMessageEdit == null) {
          errorMessageEdit = "" + "<br>";
        }
        if (eventDescriptionsN.equals("")) {
          errorMessageEdit += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u043d\u0430\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u043d\u0438\u0435 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
        if (dateValid(dateEventN) == false) {
          errorMessageEdit += " \u041d\u0435\u043a\u043e\u0440\u0440\u0435\u043a\u0442\u043d\u043e \u0437\u0430\u043f\u043e\u043b\u043d\u0435\u043d\u043d\u0430\u044f \u0434\u0430\u0442\u0430 - \u0434\u0430\u0442\u0430 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0444\u043e\u0440\u043c\u0430\u0442\u0435 \'\u0434\u0434.\u043c\u043c.\u0433\u0433\u0433\u0433\' \u0438 \u0431\u043e\u043b\u044c\u0448\u0435 \u0442\u0435\u043a\u0443\u0449\u0435\u0439!" + "<br>";
        }
        if (dateEventN.equals("")) {
          errorMessageEdit += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u0434\u0435\u043d\u044c \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
        if (eventTimeN.equals("")) {
          errorMessageEdit += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u0432\u0440\u0435\u043c\u044f \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
        if (eventBookingTimeOutN.equals("")) {
          errorMessageEdit += " \u0417\u0430\u043f\u043e\u043b\u043d\u0438\u0442\u0435 \u0432\u0440\u0435\u043c\u044f \u0443\u0434\u0430\u043b\u0435\u043d\u0438\u044f \u0431\u0440\u043e\u043d\u0438 \u043c\u0435\u0440\u043e\u043f\u0440\u0438\u044f\u0442\u0438\u044f!" + "<br>";
        }
      }
      model.addAttribute("allSectors", allSectors);
      if (errorMessageEdit != null && !errorMessageEdit.equals("")) {
        model.addAttribute("errorMessageEdit", errorMessageEdit);
        model.addAttribute("eventDescriptions", eventDescriptionsN);
        int time = Integer.parseInt(eventBookingTimeOutN);
        model.addAttribute("eventBookingTimeOut", time);
        List<Sector> sectors = sectorService.getSectorsByEvent(editEvent);
        if (sectors.size() != 0) {
          List copy = new ArrayList(sectors);
          for (Iterator<Sector> it = copy.iterator(); it.hasNext(); ) {
            Sector sector = it.next();
            model.addAttribute("id" + sector.getId(), sector.getId());
            allSectors.put(sector.getName(), sector);
          }
        }
        model.addAttribute("allSectors", allSectors);
        Date simpleDate = (Date) getDateByString(dateEventN, eventTimeN).get(0);
        model.addAttribute("dateEvent", simpleDate);
        int hour = (int) getDateByString(dateEventN, eventTimeN).get(1);
        int min = (int) getDateByString(dateEventN, eventTimeN).get(2);
        String timeEvent = "";
        if (min == 0) {
          timeEvent = "" + hour + ":" + "00";
        } else {
          timeEvent = "" + hour + ":" + min;
        }
        model.addAttribute("eventTime", timeEvent);
        model.addAttribute("eventEdit", editEvent);
        return "AddEditEvent";
      }
    }
    model.addAttribute("allSectors", allSectors);
    if (errorMessageEdit != null && !errorMessageEdit.equals("")) {
      model.addAttribute("errorMessageEdit", errorMessageEdit);
      model.addAttribute("eventDescriptions", eventDescriptionsN);
      int time = Integer.parseInt(eventBookingTimeOutN);
      model.addAttribute("eventBookingTimeOut", time);
      List<Sector> sectors = sectorService.getSectorsByEvent(editEvent);
      if (sectors.size() != 0) {
        List copy = new ArrayList(sectors);
        for (Iterator<Sector> it = copy.iterator(); it.hasNext(); ) {
          Sector sector = it.next();
          model.addAttribute("id" + sector.getId(), sector.getId());
          allSectors.put(sector.getName(), sector);
        }
      }
      model.addAttribute("allSectors", allSectors);
      Date simpleDate = (Date) getDateByString(dateEventN, eventTimeN).get(0);
      model.addAttribute("dateEvent", simpleDate);
      int hour = (int) getDateByString(dateEventN, eventTimeN).get(1);
      int min = (int) getDateByString(dateEventN, eventTimeN).get(2);
      String timeEvent = "";
      if (min == 0) {
        timeEvent = "" + hour + ":" + "00";
      } else {
        timeEvent = "" + hour + ":" + min;
      }
      model.addAttribute("eventEdit", editEvent);
      model.addAttribute("eventTime", timeEvent);
      return "AddEditEvent";
    }
    status.setComplete();
    return "redirect:/Events/Events.do";
  }

  @RequestMapping(value = "AddEditEvent/setDeleteSector.do", method = RequestMethod.POST) public String setDeleteSector(@RequestParam(value = "evnt", required = true) Long evnt, SessionStatus status) {
    Event event = eventService.getEventById(evnt);
    eventService.deleteEvent(event);
    status.setComplete();
    return "redirect:/Events/Events.do";
  }

  @SuppressWarnings(value = { "unchecked" }) @RequestMapping(value = "Events/Cancel.do", method = RequestMethod.GET) public String cancel(Model model, SessionStatus status) {
    model.addAttribute("pageName", 4);
    List<Event> events = eventService.getAllEvents();
    Collections.sort(events);
    if (events != null && events.size() > 0) {
      model.addAttribute("event", events.get(0));
      model.addAttribute("events", events);
    }
    String eventErrorMessage = (String) model.asMap().get("eventErrorMessage");
    String errorMessageEdit = (String) model.asMap().get("errorMessageEdit");
    if ((errorMessageEdit != null) || (eventErrorMessage != null)) {
      model.addAttribute("errorMessageEdit", "");
      model.addAttribute("eventErrorMessage", "");
    }
    model.addAttribute("eventDescriptions", "");
    model.addAttribute("eventBookingTimeOut", 0);
    return "Events";
  }

  @SuppressWarnings(value = { "unchecked" }) @RequestMapping(value = "NewSector/Cancel.do", method = RequestMethod.GET) public String cancelNewSector(Model model, SessionStatus status) {
    model.addAttribute("pageName", 4);
    Map allSectors = (TreeMap) model.asMap().get("allSectors");
    Date dateEv = (Date) model.asMap().get("dateEvent");
    String timeEv = (String) model.asMap().get("eventTime");
    String eventDescriptions = (String) model.asMap().get("eventDescriptions");
    Integer eventBookingTimeOut = (Integer) model.asMap().get("eventBookingTimeOut");
    model.addAttribute("dateEvent", dateEv);
    model.addAttribute("eventTime", timeEv);
    if (eventDescriptions != null) {
      model.addAttribute("eventDescriptions", eventDescriptions);
    }
    if (eventBookingTimeOut != null) {
      model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
    }
    model.addAttribute("eventEdit", editEvent);
    model.addAttribute("allSectors", allSectors);
    return "AddEditEvent";
  }

  @RequestMapping(value = "AddEditEvent/NewSector.do") public String eventsNewSector(Model model, HttpServletRequest request) {
    Map allSectors = (TreeMap) model.asMap().get("allSectors");
    Date dateEvent = (Date) model.asMap().get("dateEvent");
    String eventTime = (String) model.asMap().get("eventTime");
    String eventDescriptions = (String) model.asMap().get("eventDescriptions");
    Integer eventBookingTimeOut = (Integer) model.asMap().get("eventBookingTimeOut");
    Iterator<Sector> sectorNewList = allSectors.values().iterator();
    while (sectorNewList.hasNext()) {
      Sector sector = sectorNewList.next();
      String price = request.getParameter("price" + sector.getId());
      model.addAttribute("price" + sector.getId(), sector.getId());
      allSectors.put(sector.getName(), sector);
    }
    model.addAttribute("dateEvent", dateEvent);
    model.addAttribute("allSectors", allSectors);
    model.addAttribute("eventDescriptions", eventDescriptions);
    model.addAttribute("eventTime", eventTime);
    model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
    return "redirect:/NewSector/NewSector.do";
  }

  @RequestMapping(value = "NewSector/NewSector.do", method = RequestMethod.GET) public String sectorNew(Model model, SessionStatus status, HttpServletRequest request) {
    model.addAttribute("pageName", 4);
    Map allSectors = (TreeMap) model.asMap().get("allSectors");
    Date dateEv = (Date) model.asMap().get("dateEvent");
    String timeEv = (String) model.asMap().get("eventTime");
    String eventDescriptions = (String) model.asMap().get("eventDescriptions");
    Integer eventBookingTimeOut = (Integer) model.asMap().get("eventBookingTimeOut");
    if (editEvent != null) {
      model.addAttribute("eventEdit", editEvent);
    }
    if (allSectors != null) {
      model.addAttribute("allSectors", allSectors);
    }
    if (dateEv != null) {
      model.addAttribute("dateEvent", dateEv);
    }
    if (timeEv != null) {
      model.addAttribute("eventTime", timeEv);
    }
    if (eventDescriptions != null) {
      model.addAttribute("eventDescriptions", eventDescriptions);
    }
    if (eventBookingTimeOut != null) {
      model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
    }
    return "NewSector";
  }

  @RequestMapping(value = "NewSector/addSector.do", method = RequestMethod.POST) public String addSector(Model model, @RequestParam(value = "newPrice") String newPrice, @RequestParam(value = "sectorName") String sectorName, @RequestParam(value = "maxRows") Integer maxRows, @RequestParam(value = "maxSeats") Integer maxSeats) throws SQLException, ParseException {
    model.addAttribute("pageName", 4);
    Map allSectors = (TreeMap) model.asMap().get("allSectors");
    Date dateEv = (Date) model.asMap().get("dateEvent");
    String timeEv = (String) model.asMap().get("eventTime");
    String eventDescriptions = (String) model.asMap().get("eventDescriptions");
    Integer eventBookingTimeOut = (Integer) model.asMap().get("eventBookingTimeOut");
    double newPriceD = 0d;
    if (newPrice != null) {
      newPriceD = Double.parseDouble(newPrice);
    }
    Sector sector = new Sector();
    sector.setName(sectorName);
    sector.setMaxRows(maxRows);
    sector.setMaxSeats(maxSeats);
    sector.setPrice(newPriceD);
    sectorService.addSector(sector);
    model.addAttribute("id" + sector.getId(), sector.getId());
    if (sector != null && sector.getName() != null) {
      if (allSectors != null) {
        allSectors.put(sector.getName(), sector);
      } else {
        allSectors = new TreeMap<>();
        allSectors.put(sector.getName(), sector);
      }
    }
    if (dateEv != null) {
      model.addAttribute("dateEvent", dateEv);
    }
    if (timeEv != null) {
      model.addAttribute("eventTime", timeEv);
    }
    if (eventDescriptions != null) {
      model.addAttribute("eventDescriptions", eventDescriptions);
    }
    if (eventBookingTimeOut != null) {
      model.addAttribute("eventBookingTimeOut", eventBookingTimeOut);
    }
    if (this.editEvent != null) {
      model.addAttribute("eventEdit", editEvent);
    }
    if (allSectors != null) {
      model.addAttribute("allSectors", allSectors);
    }
    return "AddEditEvent";
  }

  public boolean dateValid(String inputDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    try {
      Date ourDate = sdf.parse(inputDate);
      Date now = new Date();
      if (ourDate.getTime() > now.getTime()) {
        return inputDate.equals(sdf.format(ourDate));
      } else {
        return false;
      }
    } catch (ParseException e) {
      return false;
    }
  }

  public boolean dateValidDate(Date inputDate) {
    Date now = new Date();
    if (inputDate.getTime() > now.getTime()) {
      return true;
    } else {
      return false;
    }
  }

  public List getDateByString(String dateEvent, String eventTime) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    Date trueDate = format.parse(dateEvent);
    int intHour = 0;
    int intMin = 0;
    if ((eventTime != null) && (eventTime != "")) {
      String[] str = eventTime.split(":");
      intHour = Integer.parseInt(str[0]);
      intMin = Integer.parseInt(str[1]);
    }
    Calendar rightAgain = Calendar.getInstance();
    rightAgain.setTime(trueDate);
    rightAgain.add(Calendar.HOUR, intHour);
    rightAgain.add(Calendar.MINUTE, intMin);
    trueDate = rightAgain.getTime();
    Date date = trueDate;
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime(date);
    int year = gc.get(GregorianCalendar.YEAR);
    int mon = gc.get(GregorianCalendar.MONTH);
    int day = gc.get(GregorianCalendar.DATE);
    int hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
    int min = gc.get(GregorianCalendar.MINUTE);
    GregorianCalendar calendarN = new GregorianCalendar();
    calendarN.set(year, mon, day, 0, 0, 0);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String localisedDate = dateFormat.format(calendarN.getTime());
    Date simpleDate = dateFormat.parse(localisedDate);
    List allDates = new ArrayList();
    allDates.add(simpleDate);
    allDates.add(hour);
    allDates.add(min);
    return allDates;
  }
}