import { extend } from './utils';

var baseUrl = '/api';
var defaultFetchOptions = {};

function normalize(arg) {
    if (!arg) {
        return '';
    }
    if (arg instanceof Object) {
        return extractId(arg);
    }
    return arg;
}

function hasId(object) {
    return object.id;
}

function extractId(object) {
    if (hasId(object)) {
        return object.id;
    }
    throw 'use yawp(id) if your endpoint does not have a @Id field called id';
}

export default (request) => {

    function yawpFn(baseArg) {

        var options = {};
        var q = {};

        class Yawp {

            constructor(props) {
                extend(this, props);
            }

            // request

            static clear() {
                options = {
                    url: normalize(baseArg)
                };
            }

            static prepareRequestOptions() {
                var _options = extend({}, options);
                Yawp.clear();
                return _options;
            }

            static baseRequest(type) {
                var options = Yawp.prepareRequestOptions();

                var url = baseUrl + options.url;
                delete options.url;

                options.method = type;
                options.json = true;
                extend(options, defaultFetchOptions);

                //console.log('r', url, options);

                return request(url, options);
            }

            // query

            static from(parentBaseArg) {
                var parentBase = normalize(parentBaseArg);
                options.url = parentBase + options.url;
                return this;
            }

            static transform(t) {
                Yawp.param('t', t);
                return this;
            }

            static where(data) {
                if (arguments.length === 1) {
                    q.where = data;
                } else {
                    q.where = [].slice.call(arguments);
                }
                return this;
            }

            static order(data) {
                q.order = data;
                return this;
            }

            static sort(data) {
                q.sort = data;
                return this;
            }

            static limit(data) {
                q.limit = data;
                return this;
            }

            static fetch(cb) {
                var promise = Yawp.baseRequest('GET');
                if (cb) {
                    return promise.then(cb);
                }
                return promise;
            }

            static setupQuery() {
                if (Object.keys(q).length > 0) {
                    Yawp.param('q', JSON.stringify(q));
                }
            }

            //static url(decode) {
            //    Yawp.setupQuery();
            //    var url = baseUrl + options.url + (options.query ? '?' + toUrlParam(options.query) : '');
            //    return decode ? decodeURIComponent(url) : url;
            //}

            static list(cb) {
                Yawp.setupQuery();
                var promise = Yawp.baseRequest('GET');
                if (cb) {
                    return promise.then(cb);
                }
                return promise;
            }

            static first(cb) {
                Yawp.limit(1);

                return Yawp.list(function (objects) {
                    var object = objects.length === 0 ? null : objects[0];
                    return cb ? cb(object) : object;
                });
            }

            static only(cb) {
                return Yawp.list(function (objects) {
                    if (objects.length !== 1) {
                        throw 'called only but got ' + objects.length + ' results';
                    }
                    var object = objects[0];
                    return cb ? cb(object) : object;
                });
            }

            // repository

            static create(object) {
                options.body = JSON.stringify(object);
                return Yawp.baseRequest('POST');
            }

            static update(object) {
                options.body = JSON.stringify(object);
                return Yawp.baseRequest('PUT');
            }

            static patch(object) {
                options.body = JSON.stringify(object);
                return Yawp.baseRequest('PATCH');
            }

            static destroy() {
                return Yawp.baseRequest('DELETE');
            }

            // actions

            static json(object) {
                options.body = JSON.stringify(object);
                return this;
            }

            static params(params) {
                options.query = extend(options.query, params);
                return this;
            }

            static param(key, value) {
                if (!options.query) {
                    options.query = {};
                }
                options.query[key] = value;
            }

            static action(verb, path) {
                options.url += '/' + path;
                return Yawp.baseRequest(verb);
            }

            static get(action) {
                return Yawp.action('GET', action);
            }

            static put(action) {
                return Yawp.action('PUT', action);
            }

            static _patch(action) {
                return Yawp.action('PATCH', action);
            }

            static post(action) {
                return Yawp.action('POST', action);
            }

            static _delete(action) {
                return Yawp.action('DELETE', action);
            }

            // instance method

            save(cb) {
                var promise = this.createOrUpdate();
                return cb ? promise.then(cb) : promise;
            }

            createOrUpdate() {
                var promise;
                if (hasId(this)) {
                    options.url = this.id;
                    promise = Yawp.update(this);
                } else {
                    promise = Yawp.create(this);
                }
                return promise;
            }

            destroy(cb) {
                options.url = extractId(this);
                var promise = Yawp.destroy();
                return cb ? promise.then(cb) : promise;
            }
        }

        Yawp.clear();
        return Yawp;
    }

    // base api

    function config(cb) {
        var c = {
            baseUrl: (url) => {
                baseUrl = url;
            },
            defaultFetchOptions: (options) => {
                defaultFetchOptions = options;
            }
        };
        cb(c);
    };

    function update(object) {
        var id = extractId(object);
        return yawpFn(id).update(object);
    }

    function patch(object) {
        var id = extractId(object);
        return yawpFn(id).patch(object);
    }

    function destroy(object) {
        var id = extractId(object);
        return yawpFn(id).destroy(object);
    }

    let baseApi = {
        config,
        update,
        patch,
        destroy
    }

    return extend(yawpFn, baseApi);
}