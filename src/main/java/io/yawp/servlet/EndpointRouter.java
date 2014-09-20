package io.yawp.servlet;

import io.yawp.repository.EndpointFeatures;
import io.yawp.repository.IdRef;
import io.yawp.repository.Repository;
import io.yawp.repository.RepositoryFeatures;
import io.yawp.repository.actions.ActionKey;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.servlet.rest.RestAction;
import io.yawp.servlet.rest.RestActionType;
import io.yawp.utils.HttpVerb;

import java.util.Map;

public class EndpointRouter {

	private Repository r;

	private RepositoryFeatures features;

	private String uri;

	private boolean overCollection;

	private ActionKey customActionKey;

	private HttpVerb verb;

	private IdRef<?> idRef;

	private Class<?> endpointClazz;

	public EndpointRouter(Repository r, HttpVerb verb, String uri) {
		this.verb = verb;
		this.uri = uri;
		this.r = r;
		this.features = r.getFeatures();
		parseUri();
		validateRestrictions();
	}

	public static EndpointRouter parse(Repository r, HttpVerb verb, String uri) {
		return new EndpointRouter(r, verb, uri);
	}

	private void parseUri() {
		this.idRef = IdRef.parse(r, verb, uri);

		this.customActionKey = parseCustomActionKey();
		this.overCollection = parseOverCollection();
		this.endpointClazz = parseEndpointClazz();
	}

	private Class<?> parseEndpointClazz() {
		String[] parts = uri.substring(1).split("/");

		if (isOverCollection()) {
			if (isCustomAction()) {
				return features.get("/" + parts[parts.length - 2]).getClazz();
			}
			return features.get("/" + parts[parts.length - 1]).getClazz();
		}

		return idRef.getClazz();
	}

	private ActionKey parseCustomActionKey() {

		if (idRef == null) {
			return rootCollectionCustomActionKey();
		}

		if (idRef.getUri().length() == uri.length()) {
			return null;
		}

		String lastToken = uri.substring(idRef.getUri().length() + 1);
		if (hasTwoParts(lastToken)) {
			return nestedCollectionCustomActionKey(lastToken);
		}

		return singleObjectCustomActionKey(lastToken);
	}

	private ActionKey singleObjectCustomActionKey(String lastToken) {
		ActionKey actionKey = new ActionKey(verb, lastToken, false);
		if (features.hasCustomAction(idRef.getClazz(), actionKey)) {
			return actionKey;
		}

		return null;
	}

	private ActionKey nestedCollectionCustomActionKey(String lastToken) {
		String[] tokens = lastToken.split("/");

		ActionKey actionKey = new ActionKey(verb, tokens[1], true);
		if (features.hasCustomAction("/" + tokens[0], actionKey)) {
			return actionKey;
		}
		return null;
	}

	private ActionKey rootCollectionCustomActionKey() {
		String[] tokens = uri.substring(1).split("/");

		if (tokens.length == 1) {
			return null;
		}

		ActionKey actionKey = new ActionKey(verb, tokens[1], true);
		if (features.hasCustomAction("/" + tokens[0], actionKey)) {
			return actionKey;
		}

		return null;
	}

	private boolean parseOverCollection() {
		if (idRef == null) {
			return true;
		}

		if (idRef.getUri().length() == uri.length()) {
			return false;
		}

		String lastToken = uri.substring(idRef.getUri().length() + 1);
		if (hasTwoParts(lastToken)) {
			return true;
		}

		ActionKey actionKey = new ActionKey(verb, lastToken, false);
		if (features.hasCustomAction(idRef.getClazz(), actionKey)) {
			return false;
		}

		return true;
	}

	private boolean hasTwoParts(String lastToken) {
		return lastToken.indexOf("/") != -1;
	}

	public boolean isOverCollection() {
		return overCollection;
	}

	public boolean isCustomAction() {
		return customActionKey != null;
	}

	public String getCustomActionName() {
		if (!isCustomAction()) {
			return null;
		}
		return customActionKey.getActionName();
	}

	public ActionKey getCustomActionKey() {
		return customActionKey;
	}

	public EndpointFeatures<?> getEndpointFeatures() {
		return features.get(endpointClazz);
	}

	public Class<?> getEndpointClazz() {
		return getEndpointFeatures().getClazz();
	}

	public IdRef<?> getIdRef() {
		return idRef;
	}

	public RestActionType getRestActionType() {
		if (isCustomAction()) {
			return RestActionType.CUSTOM;
		}
		return RestActionType.defaultRestActionType(verb, isOverCollection());
	}

	private void validateRestrictions() {
		Endpoint endpointAnnotation = getEndpointFeatures().getEndpointAnnotation();
		getRestActionType().validateRetrictions(endpointAnnotation);
	}

	public RestAction getRestAction(boolean enableHooks, Map<String, String> params) {
		try {
			Class<? extends RestAction> restActionClazz = getRestActionType().getRestActionClazz();

			if (restActionClazz == null) {
				return null;
			}

			RestAction action = restActionClazz.newInstance();

			action.setRepository(r);
			action.setEnableHooks(enableHooks);
			action.setClazz(endpointClazz);
			action.setId(idRef);
			action.setParams(params);

			return action;

		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
