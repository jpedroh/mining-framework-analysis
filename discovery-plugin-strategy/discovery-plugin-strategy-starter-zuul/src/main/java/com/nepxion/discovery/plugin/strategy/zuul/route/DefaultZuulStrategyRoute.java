package com.nepxion.discovery.plugin.strategy.zuul.route;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.CollectionUtils;
import com.nepxion.discovery.common.exception.DiscoveryException;
import com.nepxion.discovery.plugin.strategy.zuul.entity.ZuulStrategyRouteEntity;

public class DefaultZuulStrategyRoute extends SimpleRouteLocator implements ZuulStrategyRoute, RefreshableRouteLocator, ApplicationEventPublisherAware {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultZuulStrategyRoute.class);

  @Autowired(required = false) private ZuulStrategyRouteAdapter zuulStrategyRouteAdapter;

  private ZuulProperties zuulProperties;

  private ApplicationEventPublisher applicationEventPublisher;

  public DefaultZuulStrategyRoute(String servletPath, ZuulProperties zuulProperties) {
    super(servletPath, zuulProperties);
    this.zuulProperties = zuulProperties;
  }

  @Override public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @PostConstruct public void retrieve() {
    if (zuulStrategyRouteAdapter == null) {
      return;
    }
    Map<String, ZuulStrategyRouteEntity> newRouteMap = zuulStrategyRouteAdapter.retrieve();
    if (MapUtils.isEmpty(newRouteMap)) {

<<<<<<< /usr/src/app/output/nepxion/discovery/99c2bc3a686bfaa15e56484c3828e1fb0e17cfd6/discovery-plugin-strategy/discovery-plugin-strategy-starter-zuul/src/main/java/com/nepxion/discovery/plugin/strategy/zuul/route/DefaultZuulStrategyRoute.java/left.java
      throw new DiscoveryException("Zuul dynamic routes are empty");
=======
      try {
        List<ZuulStrategyRouteEntity> zuulStrategyRouteEntityList = zuulStrategyRouteAdapter.retrieve();
        update(zuulStrategyRouteEntityList);
      } catch (Exception e) {
        LOG.warn("Zuul dynamic routes can\'t be null");
      }
>>>>>>> /usr/src/app/output/nepxion/discovery/99c2bc3a686bfaa15e56484c3828e1fb0e17cfd6/discovery-plugin-strategy/discovery-plugin-strategy-starter-zuul/src/main/java/com/nepxion/discovery/plugin/strategy/zuul/route/DefaultZuulStrategyRoute.java/right.java
    }
    Map<String, ZuulProperties.ZuulRoute> routeMap = locateRoutes();
    for (Map.Entry<String, ZuulStrategyRouteEntity> entry : newRouteMap.entrySet()) {
      String path = entry.getKey();
      ZuulStrategyRouteEntity zuulStrategyRouteEntity = entry.getValue();
      if (routeMap.containsKey(path)) {
        deleteRoute(path);
      }
      ZuulProperties.ZuulRoute route = convert(zuulStrategyRouteEntity);
      addRoute(route);
    }
    LOG.info("Retrieved Zuul dynamic routes count={}", newRouteMap.size());
    applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(this));
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public void update(List<ZuulStrategyRouteEntity> zuulStrategyRouteEntityList) {
    if (zuulStrategyRouteEntityList == null) {
      throw new DiscoveryException("Zuul dynamic routes are null");
    }
    LOG.info("Updated Zuul dynamic routes={}", zuulStrategyRouteEntityList);
    Map<String, ZuulProperties.ZuulRoute> newRouteMap = zuulStrategyRouteEntityList.stream().collect(Collectors.toMap(ZuulStrategyRouteEntity::getRouteId, this::convertRoute));
    Map<String, ZuulProperties.ZuulRoute> currentRouteMap = locateRoutes();
    boolean isChanged = false;
    for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : newRouteMap.entrySet()) {
      String routeId = entry.getKey();
      ZuulRoute route = entry.getValue();
      if (!currentRouteMap.containsKey(routeId)) {
        add(route);
        isChanged = true;
      }
      if (currentRouteMap.containsKey(routeId)) {
        ZuulProperties.ZuulRoute currentRoute = currentRouteMap.get(routeId);
        if (!currentRoute.equals(route)) {
          modify(route);
          isChanged = true;
        }
      }
    }
    for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : currentRouteMap.entrySet()) {
      String routeId = entry.getKey();
      ZuulRoute route = entry.getValue();
      if (!newRouteMap.containsKey(routeId)) {
        delete(route);
        isChanged = true;
      }
    }
    if (isChanged) {
      applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(this));
    }
  }
>>>>>>> /usr/src/app/output/nepxion/discovery/99c2bc3a686bfaa15e56484c3828e1fb0e17cfd6/discovery-plugin-strategy/discovery-plugin-strategy-starter-zuul/src/main/java/com/nepxion/discovery/plugin/strategy/zuul/route/DefaultZuulStrategyRoute.java/right.java


  @Override public void add(ZuulStrategyRouteEntity zuulStrategyRouteEntity) {
    if (zuulStrategyRouteEntity == null) {
      throw new DiscoveryException("Zuul dynamic route is null");
    }
    Map<String, ZuulProperties.ZuulRoute> routeMap = locateRoutes();
    String path = zuulStrategyRouteEntity.getPath();
    if (routeMap.containsKey(path)) {
      throw new DiscoveryException("Zuul dynamic route for path=[" + path + "] exists");
    }
    ZuulProperties.ZuulRoute route = convert(zuulStrategyRouteEntity);
    addRoute(route);
    LOG.info("Added Zuul dynamic route={}", route);
    applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(this));
  }

  @Override public List<String> view(String serviceId) {
    if (StringUtils.isEmpty(serviceId)) {
      throw new DiscoveryException("ServiceId is empty");
    }
    List<String> zuulStrategyRouteList = new ArrayList<String>();
    Map<String, ZuulProperties.ZuulRoute> routeMap = locateRoutes();
    for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : routeMap.entrySet()) {
      ZuulProperties.ZuulRoute route = entry.getValue();
      if (StringUtils.equals(serviceId, route.getServiceId())) {
        zuulStrategyRouteList.add(route.toString());
      }
    }
    return zuulStrategyRouteList;
  }

  @Override public void modify(List<ZuulStrategyRouteEntity> zuulStrategyRouteEntityList) {
    if (CollectionUtils.isEmpty(zuulStrategyRouteEntityList)) {
      throw new DiscoveryException("Zuul dynamic routes are empty");
    }
    if (zuulStrategyRouteEntityList.size() != 2) {
      throw new DiscoveryException("Zuul dynamic routes size must be two");
    }
    Map<String, ZuulProperties.ZuulRoute> routeMap = locateRoutes();
    String path = zuulStrategyRouteEntityList.get(0).getPath();
    if (!routeMap.containsKey(path)) {
      throw new DiscoveryException("Zuul dynamic route for path=[" + path + "] not exists");
    }
    deleteRoute(path);
    ZuulProperties.ZuulRoute route = convert(zuulStrategyRouteEntityList.get(1));
    modifyRoute(route);
    LOG.info("Modified Zuul dynamic route={}", route);
    applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(this));
  }

  @Override public void refresh() {
    doRefresh();
  }

  @Override public void delete(String path) {
    if (StringUtils.isEmpty(path)) {
      throw new DiscoveryException("Zuul dynamic route path is empty");
    }
    Map<String, ZuulProperties.ZuulRoute> routeMap = locateRoutes();
    if (!routeMap.containsKey(path)) {
      throw new DiscoveryException("Zuul dynamic route for path=[" + path + "] not exists");
    }
    deleteRoute(path);
    LOG.info("Deleted Zuul dynamic route path={}", path);
    applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(this));
  }

  @Override public void deleteAll(String serviceId) {
    if (StringUtils.isEmpty(serviceId)) {
      throw new DiscoveryException("ServiceId is empty");
    }
    Map<String, ZuulProperties.ZuulRoute> routeMap = locateRoutes();
    for (Iterator<Map.Entry<String, ZuulProperties.ZuulRoute>> iterator = routeMap.entrySet().iterator(); iterator.hasNext(); ) {
      Map.Entry<String, ZuulProperties.ZuulRoute> entry = iterator.next();
      ZuulProperties.ZuulRoute route = entry.getValue();
      if (StringUtils.equals(serviceId, route.getServiceId())) {
        String zuulStrategyRoutePath = route.getPath();
        deleteRoute(zuulStrategyRoutePath);
      }
    }
    LOG.info("Deleted Zuul dynamic route for serviceId={}", serviceId);
    applicationEventPublisher.publishEvent(new RoutesRefreshedEvent(this));
  }

  @Override public List<String> viewAll() {
    return locateRoutes().values().stream().map(ZuulProperties.ZuulRoute::toString).collect(Collectors.toList());
  }

  private ZuulProperties.ZuulRoute convert(ZuulStrategyRouteEntity zuulStrategyRouteEntity) {
    ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute();
    route.setId(StringUtils.isNotBlank(zuulStrategyRouteEntity.getRouteId()) ? zuulStrategyRouteEntity.getRouteId() : zuulStrategyRouteEntity.getServiceName());
    route.setServiceId(zuulStrategyRouteEntity.getServiceName());
    route.setPath(zuulStrategyRouteEntity.getPath());
    route.setUrl(zuulStrategyRouteEntity.getUrl());
    route.setStripPrefix(zuulStrategyRouteEntity.isStripPrefix());
    route.setRetryable(zuulStrategyRouteEntity.getRetryable());
    route.setSensitiveHeaders(zuulStrategyRouteEntity.getSensitiveHeaders());
    route.setCustomSensitiveHeaders(zuulStrategyRouteEntity.getSensitiveHeaders() != null && !zuulStrategyRouteEntity.getSensitiveHeaders().isEmpty());
    return route;
  }

  private void addRoute(ZuulProperties.ZuulRoute route) {
    zuulProperties.getRoutes().put(route.getPath(), route);
  }

  private void modifyRoute(ZuulProperties.ZuulRoute route) {
    zuulProperties.getRoutes().put(route.getPath(), route);
  }

  private void deleteRoute(String path) {
    zuulProperties.getRoutes().remove(path);
  }
}