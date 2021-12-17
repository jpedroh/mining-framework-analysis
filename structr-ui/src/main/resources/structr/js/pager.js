/*
 * Copyright (C) 2010-2021 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Create a pager for the given type to the given DOM element.
 *
 * This pager either calls Command#list (WebSocket call) after being loaded
 * and binds Command#list to all actions or does the same for REST calls.
 *
 * If the optional callback function is given, it will be executed
 * instead of the default action.
 */
let _Pager = {
	pagerType: {},
	pageCount: {},
	page: {},
	pageSize: {},
	sortKey: {},
	sortOrder: {},
	pagerFilters: {},
	pagerExactFilterKeys: {},
	rawResultCount: [],
	pagerDataKey: 'structrPagerData_' + location.port + '_',

	initPager: function(id, type, p, ps, sort, order, filters) {
		_Pager.restorePagerData(id);

		if (_Pager.pagerType[id] === undefined) {
			_Pager.pagerType[id] = type;
		}
		if (_Pager.page[id] === undefined) {
			_Pager.page[id] = parseInt(p);
		}
		if (_Pager.pageSize[id] === undefined) {
			_Pager.pageSize[id] = parseInt(ps);
		}
		if (_Pager.sortKey[id] === undefined) {
			_Pager.sortKey[id] = sort;
		}
		if (_Pager.sortOrder[id] === undefined) {
			_Pager.sortOrder[id] = order;
		}
		if (_Pager.pagerFilters[id] === undefined) {
			_Pager.pagerFilters[id] = filters || {};
		}

		_Pager.storePagerData(id, type, _Pager.page[id], _Pager.pageSize[id], _Pager.sortKey[id], _Pager.sortOrder[id], _Pager.pagerFilters[id]);
	},

	initFilters: function(id, type, filters, exactFilterKeys) {
		_Pager.pagerFilters[id] = filters;
		_Pager.pagerExactFilterKeys[id] = exactFilterKeys;
		_Pager.storePagerData(id, type, _Pager.page[id], _Pager.pageSize[id], _Pager.sortKey[id], _Pager.sortOrder[id], _Pager.pagerFilters[id]);
	},

	forceAddFilters: function(id, type, filters) {
		_Pager.initFilters(id, type, $.extend(_Pager.pagerFilters[id], filters));
	},

	storePagerData: function(id, type, page, pageSize, sort, order, filters) {
		let data = {
			id: id,
			type: type,
			page: parseInt(page),
			pageSize: parseInt(pageSize),
			sort: sort,
			order: order,
			filters: filters
		};
		LSWrapper.setItem(_Pager.pagerDataKey + id, JSON.stringify(data));
	},

	restorePagerData: function(id) {
		let pagerData = LSWrapper.getItem(_Pager.pagerDataKey + id);
		if (pagerData) {

			if (!pagerData.startsWith('{')) {
				LSWrapper.removeItem(_Pager.pagerDataKey + id);
				return false;
			}

			pagerData = JSON.parse(pagerData);
			_Pager.pagerType[id] = pagerData.id;
			_Pager.page[id]      = pagerData.page;
			_Pager.pageSize[id]  = pagerData.pageSize;
			_Pager.sortKey[id]   = pagerData.sort;
			_Pager.sortOrder[id] = pagerData.order;
			if (pagerData.filters) {
				_Pager.pagerFilters[id] = _Pager.pagerFilters[id] || {};
				$.extend(_Pager.pagerFilters[id], pagerData.filters);
			}

			return true;
		}

		return false;
	},
	addPager: function (id, el, rootOnly, type, view, callback, optionalTransportFunction, customView, prepend) {

		let pager = new Pager(id, el, rootOnly, type, view, callback, prepend);

		pager.transportFunction = function() {
			let filterAttrs = pager.getNonEmptyFilterAttributes();

			let isExactPager = false;

			if (_Pager.pagerExactFilterKeys[id]) {

				let keysToFilter = Object.keys(filterAttrs);
				let inExactKeys  = keysToFilter.filter((k) => { return !_Pager.pagerExactFilterKeys[id].includes(k); });

				isExactPager = (inExactKeys.length === 0);
			}

			if (typeof optionalTransportFunction === "function") {
				optionalTransportFunction(id, _Pager.pageSize[id], _Pager.page[id], filterAttrs, pager.internalCallback);
			} else {
				Command.query(pager.type, _Pager.pageSize[id], _Pager.page[id], _Pager.sortKey[id], _Pager.sortOrder[id], filterAttrs, pager.internalCallback, isExactPager, view, customView);
			}
		};

		pager.init();

		return pager;
	}
};

let Pager = function (id, el, rootOnly, type, view, callback, prepend) {

	var pagerObj = this;

	// Parameters
	this.el = el;
	this.filterEl = undefined; // if set, use this as container for filters
	this.rootOnly = rootOnly;
	this.id = id;
	this.type = type;
	this.view = view;
	if (!callback) {
		this.callback = function(entities) {
			for (let entity of entities) {
				StructrModel.create(entity);
			}
		};
	} else {
		this.callback = callback;
	}

	if (typeof _Pager.pagerFilters[this.id] !== 'object') {
		_Pager.pagerFilters[this.id] = {};
	}

	this.internalCallback = function (result, count) {

		_Pager.rawResultCount[pagerObj.id] = count;
		_Pager.pageCount[pagerObj.id] = Math.max(1, Math.ceil(_Pager.rawResultCount[pagerObj.id] / _Pager.pageSize[pagerObj.id]));
		pagerObj.pageCount.val(_Pager.pageCount[pagerObj.id]);

		if (_Pager.page[pagerObj.id] < 1) {
			_Pager.page[pagerObj.id] = 1;
			pagerObj.pageNo.val(_Pager.page[pagerObj.id]);
		}

		if (_Pager.page[pagerObj.id] > _Pager.pageCount[pagerObj.id]) {
			_Pager.page[pagerObj.id] = _Pager.pageCount[pagerObj.id];
			pagerObj.pageNo.val(_Pager.page[pagerObj.id]);
		}

		pagerObj.updatePager(pagerObj.id, dialog.is(':visible') ? dialog : undefined);

		pagerObj.callback(result);
	};

	this.init = function () {

		_Pager.restorePagerData(this.id);

		let pagerHtml = '<div class="pager pager' + this.id + '" style="clear: both">'
			+ '<i class="pageLeft fa fa-angle-left"></i>'
			+ '<span class="pageWrapper">'
			+ '<input class="pageNo" value="' + _Pager.page[this.id] + '">'
			+ '<span class="of">of</span>'
			+ '<input readonly="readonly" class="readonly pageCount" type="text" size="2">'
			+ '</span>'
			+ '<i class="pageRight fa fa-angle-right"></i>'
			+ ' Items: <select class="pageSize">'
			+ '<option' + (_Pager.pageSize[this.id] === 5 ? ' selected' : '') + '>5</option>'
			+ '<option' + (_Pager.pageSize[this.id] === 10 ? ' selected' : '') + '>10</option>'
			+ '<option' + (_Pager.pageSize[this.id] === 25 ? ' selected' : '') + '>25</option>'
			+ '<option' + (_Pager.pageSize[this.id] === 50 ? ' selected' : '') + '>50</option>'
			+ '<option' + (_Pager.pageSize[this.id] === 100 ? ' selected' : '') + '>100</option>'
			+ '</select></div>';

		if (prepend === true) {
			this.el.prepend(pagerHtml);
		} else {
			this.el.append(pagerHtml);
		}

		this.pager     = $('.pager' + this.id, this.el);
		this.pageLeft  = $('.pageLeft', this.pager);
		this.pageRight = $('.pageRight', this.pager);
		this.pageNo    = $('.pageNo', this.pager);
		this.pageSize  = $('.pageSize', this.pager);
		this.pageCount = $('.pageCount', this.pager);

		this.pageSize.on('change', function(e) {
			_Pager.pageSize[pagerObj.id] = $(this).val();
			_Pager.page[pagerObj.id] = 1;
			pagerObj.updatePagerElements();
			pagerObj.transportFunction();
		});

		let limitPager = function(inputEl) {
			let val = parseInt($(inputEl).val());
			if (val < 1 || val > _Pager.pageCount[pagerObj.id]) {
				$(inputEl).val(_Pager.page[pagerObj.id]);
			} else {
				_Pager.page[pagerObj.id] = val;
			}
			pagerObj.updatePagerElements();
			pagerObj.transportFunction();
		};

		this.pageNo.on('keypress', function(e) {
			if (e.keyCode === 13) {
				limitPager(this);
			}
		});

		this.pageNo.on('change', function(e) {
			if (e.target.classList.contains('disabled')) return;
			limitPager(this);
		});

		this.pageNo.on('click', function(e) {
			if (e.target.classList.contains('disabled')) return;
			e.target.select();
		});

		this.pageLeft.on('click', function(e) {
			if (e.target.classList.contains('disabled')) return;
			_Pager.page[pagerObj.id]--;
			pagerObj.updatePagerElements();
			pagerObj.transportFunction();
		});

		this.pageRight.on('click', function(e) {
			if (e.target.classList.contains('disabled')) return;
			_Pager.page[pagerObj.id]++;
			pagerObj.updatePagerElements();
			pagerObj.transportFunction();
		});

		pagerObj.transportFunction();
	};

	/**
	 * Gets called after a new slice of data has been received
	 */
	this.updatePager = function() {

		if (_Pager.page[this.id] === 1) {
			this.pageLeft.attr('disabled', 'disabled').addClass('disabled');
		} else {
			this.pageLeft.removeAttr('disabled', 'disabled').removeClass('disabled');
		}

		if (_Pager.pageCount[this.id] === 1 || (_Pager.page[this.id] === _Pager.pageCount[this.id])) {
			this.pageRight.attr('disabled', 'disabled').addClass('disabled');
		} else {
			this.pageRight.removeAttr('disabled', 'disabled').removeClass('disabled');
		}

		if (_Pager.pageCount[this.id] === 1) {
			this.pageNo.attr('disabled', 'disabled').addClass('disabled');
		} else {
			this.pageNo.removeAttr('disabled', 'disabled').removeClass('disabled');
		}

		_Pager.storePagerData(this.id, _Pager.pagerType[this.id], _Pager.page[this.id], _Pager.pageSize[this.id], _Pager.sortKey[this.id], _Pager.sortOrder[this.id], _Pager.pagerFilters[this.id]);
	};

	/**
	 * Gets called whenever a change has been made (i.e. button has been pressed)
	 */
	this.updatePagerElements = function () {
		$('.pageNo',   this.pager).val(_Pager.page[this.id]);
		$('.pageSize', this.pager).val(_Pager.pageSize[this.id]);

		this.cleanupFunction();
	};

	/**
	 * the default Pager
	 * @returns {undefined}
	 */
	this.transportFunction = function () {
		console.warn('default implementation does nothing!');
	};

	/**
	 * by default all node elements are removed
	 * specialized implementations can just override this method
	 */
	this.cleanupFunction = function () {
		$('.node', pagerObj.el).remove();
	};

	/**
	 * activate the filter elements for the pager
	 *
	 * must be called after the pager has been initialized because the
	 * filters don't necessarily exist at the time the pager is created
	 */
	this.activateFilterElements = function (filterContainer) {

		// If filterContainer is given, set as filter element. Default is the pager container itself.
		this.filterEl = filterContainer || pagerObj.pager;

		let foundFilters = [];

		$('input.filter[type=text]', this.filterEl).each(function (idx, elem) {
			let $elem           = $(elem);
			let filterAttribute = $elem.data('attribute');

			foundFilters.push(filterAttribute);

			if (_Pager.pagerFilters[pagerObj.id][filterAttribute]) {
				$elem.val(_Pager.pagerFilters[pagerObj.id][filterAttribute]);
			}
		});

		$('input.filter[type=text]', this.filterEl).on('keyup', function(e) {
			let $filterEl = $(this);
			let filterAttribute = $filterEl.data('attribute');

			if (e.keyCode === 13) {

				if (filterAttribute && filterAttribute.length) {

					let filterVal = $filterEl.val();

					if (filterVal === '') {
						_Pager.pagerFilters[pagerObj.id][filterAttribute] = null;
					} else {
						_Pager.pagerFilters[pagerObj.id][filterAttribute] = filterVal;
					}

					_Pager.page[pagerObj.id] = 1;
					_Pager.pagerFilters[pagerObj.id][filterAttribute] = filterVal;
					pagerObj.updatePagerElements();
					pagerObj.transportFunction();
				}

			} else if (e.keyCode === 27) {

				// allow ESC in pagers in dialogs (do not close dialog on ESC while focus in input)
				e.preventDefault();
				e.stopPropagation();

				_Pager.pagerFilters[pagerObj.id][filterAttribute] = null;
				$filterEl.val('');

				_Pager.page[pagerObj.id] = 1;
				pagerObj.updatePagerElements();
				pagerObj.transportFunction();
			}
		});

		$('input.filter[type=text]', this.filterEl).on('blur', function(e) {
			let $filterEl       = $(this);
			let filterAttribute = $filterEl.data('attribute');
			let filterVal       = $filterEl.val();
			let lastFilterValue = _Pager.pagerFilters[pagerObj.id][filterAttribute];

			if (filterVal === '') {
				_Pager.pagerFilters[pagerObj.id][filterAttribute] = null;
			} else {
				_Pager.pagerFilters[pagerObj.id][filterAttribute] = filterVal;
			}

			if (filterAttribute && filterAttribute.length) {

				if (lastFilterValue !== filterVal && !(filterVal === '' && lastFilterValue === null)) {
					_Pager.page[pagerObj.id] = 1;
					pagerObj.updatePagerElements();
					pagerObj.transportFunction();
				}

			} else {

				_Pager.pagerFilters[pagerObj.id][filterAttribute] = null;
				$filterEl.val('');

				_Pager.page[pagerObj.id] = 1;
				pagerObj.updatePagerElements();
				pagerObj.transportFunction();
			}
		});

		$('input.filter[type=checkbox]', this.filterEl).each(function (idx, elem) {
			let $elem           = $(elem);
			let filterAttribute = $elem.data('attribute');

			foundFilters.push(filterAttribute);

			if (_Pager.pagerFilters[pagerObj.id][filterAttribute]) {
				$elem.prop('checked', _Pager.pagerFilters[pagerObj.id][filterAttribute]);
			}
		});

		$('input.filter[type=checkbox]', this.filterEl).on('change', function(e) {
			let $filterEl       = $(this);
			let filterAttribute = $filterEl.data('attribute');

			if (filterAttribute && filterAttribute.length) {
				_Pager.pagerFilters[pagerObj.id][filterAttribute] = $filterEl.prop('checked');

				_Pager.page[pagerObj.id] = 1;
				pagerObj.updatePagerElements();
				pagerObj.transportFunction();
			}
		});

		if (Structr.isInMemoryDatabase) {
			
			// remove filters which are either removed or temporarily disabled due to database limitations
			// for example resource access grants filtering for "active" grants
			let storedKeys = Object.keys(_Pager.pagerFilters[pagerObj.id]);
			let update = false;

			for (let key of storedKeys) {
				if (foundFilters.indexOf(key) === -1) {
					update = true;
					delete _Pager.pagerFilters[pagerObj.id][key];
				}
			}

			if (update) {
				this.transportFunction();
			}
		}

	};

	/**
	 * @returns the non-empty filter attributes
	 */
	this.getNonEmptyFilterAttributes = function () {
		let nonEmptyFilters = {};

		for (let fa in _Pager.pagerFilters[pagerObj.id]) {
			if (_Pager.pagerFilters[pagerObj.id][fa] !== null && _Pager.pagerFilters[pagerObj.id][fa] !== "") {
				nonEmptyFilters[fa] = _Pager.pagerFilters[pagerObj.id][fa];
			}
		}

		return nonEmptyFilters;
	};

	/**
	 *
	 * @param {type} key The key to sort by
	 */
	this.setSortKey = function (key) {
		if (_Pager.sortKey[pagerObj.id] === key) {

			// invert sort order
			if (_Pager.sortOrder[pagerObj.id] === "asc") {
				_Pager.sortOrder[pagerObj.id] = "desc";
			} else {
				_Pager.sortOrder[pagerObj.id] = "asc";
			}

		} else {
			_Pager.sortKey[pagerObj.id] = key;
			_Pager.sortOrder[pagerObj.id] = "asc";
		}

		_Pager.page[pagerObj.id] = 1;
		pagerObj.updatePagerElements();
		pagerObj.transportFunction();
	};

	/**
	 * Refresh the currently displayed page
	 */
	this.refresh = function () {
		pagerObj.updatePagerElements();
		pagerObj.transportFunction();
	};
};
