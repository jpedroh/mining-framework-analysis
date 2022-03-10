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
var main;
var drop;
var selectedElements = [];
var folderPageSize = 10000, folderPage = 1;
var filesViewModeKey = 'structrFilesViewMode_' + location.port;
var timeout, attempts = 0, maxRetry = 10;
var displayingFavorites = false;
var filesLastOpenFolderKey = 'structrFilesLastOpenFolder_' + location.port;
var filesResizerLeftKey = 'structrFilesResizerLeftKey_' + location.port;
var activeFileTabPrefix = 'activeFileTabPrefix' + location.port;

$(document).ready(function() {
	Structr.registerModule(_Files);
});

let _Files = {
	_moduleName: 'files',
	_viewMode: LSWrapper.getItem(filesViewModeKey) || 'list',
	defaultFolderAttributes: 'id,name,type,owner,isFolder,path,visibleToPublicUsers,visibleToAuthenticatedUsers,ownerId,isMounted,parentId,foldersCount,filesCount',
	searchField: undefined,
	searchFieldClearIcon: undefined,
	currentWorkingDir: undefined,
	filesMain: undefined,
	fileTree: undefined,
	folderContents: undefined,
	fileUploadList: undefined,
	chunkSize: 1024 * 64,
	fileSizeLimit: 1024 * 1024 * 1024,
	activeFileId: undefined,
	currentEditor: undefined,
	fileContents: {},
	fileHasUnsavedChanges: {},
	getViewMode: function () {
		return _Files._viewMode || 'list';
	},
	setViewMode: function(viewMode) {
		_Files._viewMode = viewMode;
		LSWrapper.setItem(filesViewModeKey, viewMode);
	},
	isViewModeActive: function(viewMode) {
		return (viewMode === _Files.getViewMode());
	},
	init: function() {

		_Files.setViewMode(LSWrapper.getItem(filesViewModeKey) || 'list');

		Structr.makePagesMenuDroppable();
		Structr.adaptUiToAvailableFeatures();

		window.addEventListener('resize', _Files.resize);
	},
	resize: function() {
		if (Structr.isModuleActive(_Files)) {
			_Files.moveResizer();
			Structr.resize();
			$('div.xml-mapping').css({ height: dialogBox.height() - 118 });
		}
	},
	moveResizer: function(left) {

		// throttle
		requestAnimationFrame(() => {
			left = left || LSWrapper.getItem(filesResizerLeftKey) || 300;
			$('.column-resizer', _Files.filesMain).css({ left: left });

			_Files.fileTree.css({width: left - 14 + 'px'});
			$('#folder-contents-container').css({width: 'calc(100% - ' + (left + 14) + 'px)'});
		});
	},
	onload: function() {

		Structr.fetchHtmlTemplate('files/files', {}, async (html) => {

			main[0].innerHTML = html;

			_Files.init();

			Structr.updateMainHelpLink(Structr.getDocumentationURLForTopic('files'));

			_Files.filesMain      = $('#files-main');
			_Files.fileTree       = $('#file-tree');
			_Files.folderContents = $('#folder-contents');

			_Files.moveResizer();
			Structr.initVerticalSlider($('.column-resizer', _Files.filesMain), filesResizerLeftKey, 204, _Files.moveResizer);

			let initFunctionBar = async () => {

				let fileTypes   = await _Schema.getDerivedTypes('org.structr.dynamic.File', ['CsvFile']);
				let folderTypes = await _Schema.getDerivedTypes('org.structr.dynamic.Folder', ['Trash']);

				Structr.fetchHtmlTemplate('files/functions', { fileTypes, folderTypes }, async (html) => {

					Structr.functionBar.innerHTML = html;

					UISettings.showSettingsForCurrentModule();

					let fileTypeSelect   = document.querySelector('select#file-type');
					let addFileButton    = document.getElementById('add-file-button');
					let folderTypeSelect = document.querySelector('select#folder-type');
					let addFolderButton  = document.getElementById('add-folder-button');

					addFileButton.addEventListener('click', () => {
						Command.create({
							type: fileTypeSelect.value,
							size: 0,
							parentId: _Files.currentWorkingDir ? _Files.currentWorkingDir.id : null
						});
					});

					addFolderButton.addEventListener('click', () => {
						Command.create({
							type: folderTypeSelect.value,
							parentId: _Files.currentWorkingDir ? _Files.currentWorkingDir.id : null
						});
					});

					Structr.functionBar.querySelector('.mount_folder').addEventListener('click', _Files.openMountDialog);

					_Files.searchField = Structr.functionBar.querySelector('#files-search-box');

					_Files.searchFieldClearIcon = document.querySelector('.clearSearchIcon');
					_Files.searchFieldClearIcon.addEventListener('click', (e) => {
						_Files.clearSearch();
					});

					_Files.searchField.focus();

					_Files.searchField.addEventListener('keyup', (e) => {

						let searchString = _Files.searchField.value;

						if (searchString && searchString.length) {
							_Files.searchFieldClearIcon.style.display = 'block';
						}

						if (searchString && searchString.length && e.keyCode === 13) {

							_Files.fulltextSearch(searchString);

						} else if (e.keyCode === 27 || searchString === '') {
							_Files.clearSearch();
						}
					});
				});
			};
			initFunctionBar(); // run async (do not await) so it can execute while jstree is initialized

			$.jstree.defaults.core.themes.dots      = false;
			$.jstree.defaults.dnd.inside_pos        = 'last';
			$.jstree.defaults.dnd.large_drop_target = true;

			_Files.fileTree.on('ready.jstree', function () {

				_TreeHelper.makeTreeElementDroppable(_Files.fileTree, 'root');
				_TreeHelper.makeTreeElementDroppable(_Files.fileTree, 'favorites');

				_Files.loadAndSetWorkingDir(function () {

					let lastOpenFolder = LSWrapper.getItem(filesLastOpenFolderKey);

					if (lastOpenFolder === 'favorites') {

						$('#favorites_anchor').click();

					} else if (_Files.currentWorkingDir) {

						_Files.deepOpen(_Files.currentWorkingDir);

					} else {

						let selectedNode = _Files.fileTree.jstree('get_selected');
						if (selectedNode.length === 0) {
							$('#root_anchor').click();
						}
					}
				});
			});

			_Files.fileTree.on('select_node.jstree', function (evt, data) {

				if (data.node.id === 'favorites') {

					_Files.displayFolderContents('favorites');

				} else {

					_Files.setWorkingDirectory(data.node.id);
					_Files.displayFolderContents(data.node.id, data.node.parent, data.node.original.path, data.node.parents);
				}
			});

			_TreeHelper.initTree(_Files.fileTree, _Files.treeInitFunction, 'structr-ui-filesystem');

			_Files.activateUpload();

			$(window).off('resize').resize(function () {
				_Files.resize();
			});

			Structr.unblockMenu(100);

			_Files.resize();
			Structr.adaptUiToAvailableFeatures();
		});
	},
	getContextMenuElements: function (div, entity) {

		const isFile             = entity.isFile;
		const isFolder           = entity.isFolder;
		let selectedElements     = document.querySelectorAll('.node.selected');

		// there is a difference when right-clicking versus clicking the kebab icon
		let fileNode = (div.hasClass('node') ? div : $('.node', div));

		if (!fileNode.hasClass('selected')) {
			for (let selNode of document.querySelectorAll('.node.selected')) {
				selNode.classList.remove('selected');
			}
			fileNode.addClass('selected');

			selectedElements = document.querySelectorAll('.node.selected');
		}

		let fileCount     = document.querySelectorAll('.node.file.selected').length;
		let isMultiSelect = selectedElements.length > 1;
		let elements      = [];
		let contentType   = entity.contentType || '';

		if (isFile) {

			if (entity.isImage && contentType !== 'text/svg' && !contentType.startsWith('image/svg')) {

				if (entity.isTemplate) {
					elements.push({
						icon: _Icons.getSvgIcon('pencil_edit'),
						name: 'Edit source',
						clickHandler: function () {
							_Files.editFile(entity);
							return false;
						}
					});
				} else {
					elements.push({
						icon: _Icons.getSvgIcon('pencil_edit'),
						name: 'Edit Image',
						clickHandler: function () {
							_Files.editImage(entity);
							return false;
						}
					});
				}

			} else if (fileCount === 1 && _Files.isMinificationTarget(entity)) {
				elements.push({
					// icon: '<i class="' + _Icons.getFullSpriteClass(_Icons.getMinificationIcon(entity)) + '" ></i>',
					name: 'Edit Minification',
					clickHandler: function () {
						_Minification.showMinificationDialog(entity);
						return false;
					}
				});

			} else {
				elements.push({
					icon: _Icons.getSvgIcon('pencil_edit'),
					name: 'Edit File' + ((fileCount > 1) ? 's' : ''),
					clickHandler: function () {
						_Files.editFile(entity);
						return false;
					}
				});
			}

			_Elements.appendContextMenuSeparator(elements);
		}

		elements.push({
			name: 'Basic',
			clickHandler: function() {
				_Entities.showProperties(entity, 'general');
				return false;
			}
		});

		elements.push({
			name: 'Properties',
			clickHandler: function() {
				_Entities.showProperties(entity, 'ui');
				return false;
			}
		});

		_Elements.appendContextMenuSeparator(elements);

		if (isFile) {

			if (displayingFavorites) {
				elements.push({
					icon: _Icons.getSvgIcon('favorite-star-remove'),
					name: 'Remove from Favorites',
					clickHandler: function () {

						for (let el of selectedElements) {
							let id = Structr.getId(el);

							Command.favorites('remove', id, () => {
								Structr.node(id).remove();
							});
						}
						return false;
					}
				});

			} else if (entity.isFavoritable) {

				elements.push({
					icon: _Icons.getSvgIcon('favorite-star'),
					name: 'Add to Favorites',
					clickHandler: function () {

						for (let el of selectedElements) {
							let obj = StructrModel.obj(Structr.getId(el));

							if (obj.isFavoritable) {
								Command.favorites('add', obj.id, () => {});
							}
						}

						return false;
					}
				});
			}

			if (fileCount === 1) {
				elements.push({
					name: 'Copy Download URL',
					clickHandler: function () {
						// do not make the click handler async because it would return a promise instead of the boolean

						(async () => {
							// fake the a element so we do not need to look up the server
							let a = document.createElement('a');
							let possiblyUpdatedEntity = StructrModel.obj(entity.id);
							a.href = possiblyUpdatedEntity.path;
							await navigator.clipboard.writeText(a.href);
						})();
						return false;
					}
				});
			}

			if (fileCount === 1 && _Files.isArchive(entity)) {
				elements.push({
					name: 'Unpack archive',
					clickHandler: function () {
						_Files.unpackArchive(entity);
						return false;
					}
				});
			}

			Structr.performModuleDependendAction(function () {
				if (fileCount === 1 && Structr.isModulePresent('csv') && Structr.isModulePresent('api-builder') && contentType === 'text/csv') {
					elements.push({
						// icon: '<i class="' + _Icons.getFullSpriteClass(_Icons.import_icon) + '"></i>',
						name: 'Import CSV',
						clickHandler: function () {
							Importer.importCSVDialog(entity, false);
							return false;
						}
					});
				}
			});

			Structr.performModuleDependendAction(function () {
				if (fileCount === 1 && Structr.isModulePresent('xml') && (contentType === 'text/xml' || contentType === 'application/xml')) {
					elements.push({
						// icon: '<i class="' + _Icons.getFullSpriteClass(_Icons.import_icon) + '"></i>',
						name: 'Import XML',
						clickHandler: function () {
							Importer.importXMLDialog(entity, false);
							return false;
						}
					});
				}
			});
		}

		if (!isMultiSelect) {

			_Elements.appendContextMenuSeparator(elements);

			_Elements.appendSecurityContextMenuItems(elements, entity, entity.isFolder);
		}

		_Elements.appendContextMenuSeparator(elements);

		elements.push({
			icon: _Icons.getSvgIcon('trashcan'),
			classes: ['menu-bolder', 'danger'],
			name: 'Delete ' + (isMultiSelect ? 'selected' : entity.type),
			clickHandler: () => {

				let files = [];

				for (let el of selectedElements) {
					files.push(Structr.entityFromElement(el));
				}

				_Entities.deleteNodes(this, files, true, () => {
					_Files.refreshTree();
				});

				return false;
			}
		});

		_Elements.appendContextMenuSeparator(elements);

		return elements;
	},
	formatBytes(a, b= 2) {

		const sizes = ["Bytes","KB","MB","GB","TB","PB","EB","ZB","YB"];

		if (0 === a) return "0 " + sizes[0];

		const c = (0 > b) ? 0 : b;
		const d = Math.floor(Math.log(a) / Math.log(1024));

		return parseFloat((a/Math.pow(1024,d)).toFixed(c)) + " " + sizes[d]
	},
	deepOpen: function(d, dirs) {

		_TreeHelper.deepOpen(_Files.fileTree, d, dirs, 'parent', (_Files.currentWorkingDir ? _Files.currentWorkingDir.id : 'root'));

	},
	refreshTree: function() {

		let selectedId = _Files.fileTree.jstree('get_selected');

		_TreeHelper.refreshTree(_Files.fileTree, function() {
			_TreeHelper.makeTreeElementDroppable(_Files.fileTree, 'root');
			_TreeHelper.makeTreeElementDroppable(_Files.fileTree, 'favorites');

			_Files.fileTree.jstree('deselect_all');
			_Files.fileTree.jstree('activate_node', selectedId);
		});
	},
	refreshNode: function(nodeId, newName) {

		let node = _Files.fileTree.jstree('get_node', nodeId);
		node.text = newName;

		_TreeHelper.refreshNode(_Files.fileTree, node);
	},
	treeInitFunction: function(obj, callback) {

		switch (obj.id) {

			case '#':

				let defaultFilesystemEntries = [
					{
						id: 'favorites',
						text: 'Favorite Files',
						children: false,
						icon: _Icons.star_icon
					},
					{
						id: 'root',
						text: '/',
						children: true,
						icon: _Icons.structr_logo_small,
						path: '/',
						state: {
							opened: true
						}
					}
				];

				callback(defaultFilesystemEntries);
				break;

			case 'root':
				_Files.load(null, callback);
				break;

			default:
				_Files.load(obj.id, callback);
				break;
		}

	},
	unload: function() {
		window.removeEventListener('resize', _Files.resize);
		fastRemoveAllChildren($('#files-main', main)[0]);
		fastRemoveAllChildren(Structr.functionBar);
	},
	activateUpload: function() {

		if (window.File && window.FileReader && window.FileList && window.Blob) {

			drop = $('#folder-contents');

			drop.on('dragover', function(event) {
				event.originalEvent.dataTransfer.dropEffect = 'copy';
				return false;
			});

			drop.on('drop', function(event) {

				if (!event.originalEvent.dataTransfer) {
					return;
				}

				event.stopPropagation();
				event.preventDefault();

				if (displayingFavorites === true) {
					(new MessageBuilder()).warning("Can't upload to virtual folder Favorites - please first upload file to destination folder and then drag to favorites.").show();
					return;
				}

				_Files.fileUploadList = event.originalEvent.dataTransfer.files;
				let filesToUpload = [];
				let tooLargeFiles = [];

				$(_Files.fileUploadList).each(function(i, file) {
					if (file.size <= _Files.fileSizeLimit) {
						filesToUpload.push(file);
					} else {
						tooLargeFiles.push(file);
					}
				});

				if (filesToUpload.length < _Files.fileUploadList.length) {

					let errorText = 'The following files are too large (limit ' + _Files.fileSizeLimit / (1024 * 1024) + ' Mbytes):<br>\n';

					$(tooLargeFiles).each(function(i, tooLargeFile) {
						errorText += '<b>' + tooLargeFile.name + '</b>: ' + Math.round(tooLargeFile.size / (1024 * 1024)) + ' Mbytes<br>\n';
					});

					new MessageBuilder().error(errorText).title('File(s) too large for upload').requiresConfirmation().show();
				}

				filesToUpload.forEach(function(fileToUpload) {
					fileToUpload.parentId = _Files.currentWorkingDir ? _Files.currentWorkingDir.id : null;
					fileToUpload.hasParent = true; // Setting hasParent = true forces the backend to upload the file to the root dir even if parentId is null

					Command.createFile(fileToUpload, (createdFileNode) => {
						fileToUpload.id = createdFileNode.id;
						_Files.uploadFile(createdFileNode);
					});
				});

				return false;
			});
		}
	},
	uploadFile: function(file) {
		let worker = new Worker('js/upload-worker.js');
		worker.onmessage = function(e) {

			let binaryContent = e.data;
			let fileSize      = e.data.byteLength;
			let node          = Structr.node(file.id);
			node.find('.size').text(fileSize);

			let chunks = Math.ceil(fileSize / _Files.chunkSize);

			for (let c = 0; c < chunks; c++) {
				let start = c * _Files.chunkSize;
				let end = (c + 1) * _Files.chunkSize;
				let chunk = window.btoa(String.fromCharCode.apply(null, new Uint8Array(binaryContent.slice(start, end))));
				Command.chunk(file.id, c, _Files.chunkSize, chunk, chunks);
			}
		};

		$(_Files.fileUploadList).each(function(i, fileObj) {
			if (file.id === fileObj.id) {
				worker.postMessage(fileObj);
			}
		});
	},
	fulltextSearch: function(searchString) {

		let content = $('#folder-contents');
		content.children().hide();

		let url = Structr.rootUrl + 'files/ui?' + Structr.getRequestParameterName('loose') + '=1';

		for (let str of searchString.split(' ')) {
			url = url + '&indexedWords=' + str;
		}

		_Files.displaySearchResultsForURL(url, searchString);
	},
	clearSearch: function() {
		_Files.searchField.value = '';
		_Files.searchFieldClearIcon.style.display = 'none';
		$('#search-results').remove();
		$('#folder-contents').children().show();
	},
	loadAndSetWorkingDir: function(callback) {
		Command.rest("/me/ui", function (result) {
			let me = result[0];
			if (me.workingDirectory) {
				_Files.currentWorkingDir = me.workingDirectory;
			} else {
				_Files.currentWorkingDir = null;
			}

			callback();
		});
	},
	load: function(id, callback) {

		let displayFunction = function (folders) {

			let list = folders.map((d) => {
				return {
					id: d.id,
					text:  d.name || '[unnamed]',
					children: d.foldersCount > 0,
					icon: 'fa fa-folder',
					path: d.path
				};
			});

			callback(list);

			_TreeHelper.makeDroppable(_Files.fileTree, list);
		};

		if (!id) {
			Command.list('Folder', true, folderPageSize, folderPage, 'name', 'asc', _Files.defaultFolderAttributes, displayFunction);
		} else {
			Command.query('Folder', folderPageSize, folderPage, 'name', 'asc', {parent: id}, displayFunction, true, 'public', _Files.defaultFolderAttributes);
		}
	},
	setWorkingDirectory: function(id) {

		if (id === 'root') {
			_Files.currentWorkingDir = null;
		} else {
			_Files.currentWorkingDir = { id: id };
		}

		$.ajax({
			url: Structr.rootUrl + 'me',
			dataType: 'json',
			contentType: 'application/json; UTF-8',
			type: 'PUT',
			data: JSON.stringify({'workingDirectory': _Files.currentWorkingDir})
		});
	},
	registerFolderLinks: function() {

		$('.is-folder.file-icon', _Files.folderContents).off('click').on('click', function (e) {
			e.preventDefault();
			e.stopPropagation();

			let el = $(this);
			let targetId = el.data('targetId');

			let openTargetNode = () => {
				_Files.fileTree.jstree('open_node', targetId, () => {
					_Files.fileTree.jstree('activate_node', targetId);
				});
			};

			let parentId = el.data('parentId');

			if (!parentId || _Files.fileTree.jstree('is_open', parentId)) {
				openTargetNode();
			} else {
				_Files.fileTree.jstree('open_node', parentId, openTargetNode);
			}

		});
	},
	updateFunctionBarStatus: (displayingFavorites) => {

		let addFolderButton   = document.getElementById('add-folder-button');
		let addFileButton     = document.getElementById('add-file-button');
		let mountDialogButton = document.getElementById('mount-folder-dialog-button');

		if (displayingFavorites) {

			addFolderButton?.classList.add('disabled');
			addFileButton?.classList.add('disabled');
			mountDialogButton?.classList.add('disabled');

			addFolderButton?.setAttribute('disabled', true);
			addFileButton?.setAttribute('disabled', true);
			mountDialogButton?.setAttribute('disabled', true);

		} else {

			addFolderButton?.classList.remove('disabled');
			addFileButton?.classList.remove('disabled');
			mountDialogButton?.classList.remove('disabled');

			addFolderButton?.removeAttribute('disabled');
			addFileButton?.removeAttribute('disabled');
			mountDialogButton?.removeAttribute('disabled');
		}
	},
	displayFolderContents: function(id, parentId, nodePath, parents) {

		fastRemoveAllChildren(_Files.folderContents[0]);

		LSWrapper.setItem(filesLastOpenFolderKey, id);

		displayingFavorites = (id === 'favorites');
		let isRootFolder    = (id === 'root');
		let parentIsRoot    = (parentId === '#');

		_Files.updateFunctionBarStatus(displayingFavorites);
		_Files.insertLayoutSwitches(id, parentId, nodePath, parents);

		// store current folder id so we can filter slow requests
		_Files.folderContents.data('currentFolder', id);

		let handleChildren = (children) => {

			let currentFolder = _Files.folderContents.data('currentFolder');

			if (currentFolder === id) {

				if (children && children.length) {
					for (let child of children) {
						_Files.appendFileOrFolder(child);
					}
				}

				_Files.resize();
				_Files.registerFolderLinks();
			}
		};

		if (displayingFavorites === true) {

			$('#folder-contents-container > button').addClass('disabled').attr('disabled', 'disabled');

			_Files.folderContents.append('<div class="folder-path truncate"><i class="' + _Icons.getFullSpriteClass(_Icons.star_icon) + '" /> Favorite Files</div>');

			if (_Files.isViewModeActive('list')) {

				_Files.folderContents.append('<table id="files-table" class="stripe"><thead><tr><th class="icon">&nbsp;</th><th>Name</th><th></th><th>Size</th><th>Type</th><th>Owner</th></tr></thead>'
					+ '<tbody id="files-table-body"></tbody></table>');
			}

			$.ajax({
				url: Structr.rootUrl + 'me/favorites',
				statusCode: {
					200: function(data) {
						handleChildren(data.result);
					}
				}
			});

		} else {

			$('#folder-contents-container > button').removeClass('disabled').attr('disabled', null);

			if (isRootFolder) {
				Command.list('Folder', true, 1000, 1, 'name', 'asc', _Files.defaultFolderAttributes, handleChildren);
			} else {
				Command.query('Folder', 1000, 1, 'name', 'asc', {parentId: id}, handleChildren, true, null, _Files.defaultFolderAttributes);
			}

			_Pager.initPager('filesystem-files', 'File', 1, 25, 'name', 'asc');
			_Pager.page['File'] = 1;

			let filterOptions = {
				parentId: (parentIsRoot ? '' : id),
				hasParent: (!parentIsRoot)
			};

			let pagerId = 'filesystem-files';
			_Pager.initFilters(pagerId, 'File', filterOptions, ['parentId', 'hasParent', 'isThumbnail']);

			let filesPager = _Pager.addPager(pagerId, _Files.folderContents, false, 'File', 'public', handleChildren, null, 'id,name,type,contentType,isFile,isImage,isThumbnail,isFavoritable,isTemplate,tnSmall,tnMid,path,size,owner,visibleToPublicUsers,visibleToAuthenticatedUsers');

			filesPager.cleanupFunction = () => {
				let toRemove = $('.node.file', filesPager.el).closest( (_Files.isViewModeActive('list') ? 'tr' : '.tile') );

				for (let elem of toRemove) {
					fastRemoveAllChildren(elem);
					elem.remove();
				}
			};

			filesPager.pager.append('Filter: <input type="text" class="filter" data-attribute="name">');
			filesPager.pager.append('<input type="text" class="filter" data-attribute="parentId" value="' + (parentIsRoot ? '' : id) + '" hidden>');
			filesPager.pager.append('<input type="checkbox" class="filter" data-attribute="hasParent" ' + (parentIsRoot ? '' : 'checked') + ' hidden>');
			filesPager.activateFilterElements();

			_Files.insertBreadCrumbNavigation(parents, nodePath, id);

			if (_Files.isViewModeActive('list')) {
				_Files.folderContents.append('<table id="files-table" class="stripe"><thead><tr><th class="icon">&nbsp;</th><th>Name</th><th></th><th>Size</th><th>Type</th><th>Owner</th></tr></thead>'
					+ '<tbody id="files-table-body">'
					+ (!isRootFolder ? '<tr><td class="is-folder file-icon" data-target-id="' + parentId + '"><i class="fa fa-folder"></i></td><td><a href="#" class="folder-up">..</a></td><td></td><td></td><td></td></tr>' : '')
					+ '</tbody></table>');

			} else if (_Files.isViewModeActive('tiles')) {
				if (!isRootFolder) {
					_Files.folderContents.append('<div class="tile"><div class="node folder"><div class="is-folder file-icon" data-target-id="' + parentId + '"><i class="fa fa-folder"></i></div><b title="..">..</b></div></div>');
				}
			} else if (_Files.isViewModeActive('img')) {
				if (!isRootFolder) {
					_Files.folderContents.append('<div class="tile img-tile"><div class="node folder"><div class="is-folder file-icon" data-target-id="' + parentId + '"><i class="fa fa-folder"></i></div><b title="..">..</b></div></div>');
				}
			}
		}
	},
	insertBreadCrumbNavigation: function(parents, nodePath, id) {

		if (parents) {

			let preventOldFolderNameInBreadcrumbs = () => {
				let modelObj = StructrModel.obj(id);
				if (modelObj && modelObj.path) {
					nodePath = modelObj.path;
				}
			};
			preventOldFolderNameInBreadcrumbs();

			parents = [].concat(parents).reverse().slice(1);

			let pathNames = (nodePath === '/') ? ['/'] : [''].concat(nodePath.slice(1).split('/'));
			let path      = parents.map((parent, idx) => { return '<a class="breadcrumb-entry" data-folder-id="' + parent + '">' + pathNames[idx] + '/</a>'; }).join('') + pathNames.pop();

			_Files.folderContents.append('<div class="folder-path truncate">' + path + '</div>');

			$('.breadcrumb-entry').click(function (e) {
				e.preventDefault();

				$('#' + $(this).data('folderId') + '_anchor').click();
			});
		}
	},
	insertLayoutSwitches: function (id, parentId, nodePath, parents) {

		let checkmark = _Icons.getSvgIcon('checkmark_bold', 12, 12, 'icon-green mr-2');

		_Files.folderContents.prepend('<div id="switches">'
			+ '<button class="switch ' + (_Files.isViewModeActive('list') ? 'active' : 'inactive') + ' inline-flex items-center" id="switch-list" data-view-mode="list">' + (_Files.isViewModeActive('list') ? checkmark : '') + ' List</button>'
			+ '<button class="switch ' + (_Files.isViewModeActive('tiles') ? 'active' : 'inactive') + ' inline-flex items-center" id="switch-tiles" data-view-mode="tiles">' + (_Files.isViewModeActive('tiles') ? checkmark : '') + ' Tiles</button>'
			+ '<button class="switch ' + (_Files.isViewModeActive('img') ? 'active' : 'inactive') + ' inline-flex items-center" id="switch-img" data-view-mode="img">' + (_Files.isViewModeActive('img') ? checkmark : '') + ' Images</button>'
			+ '</div>');

		let listSw  = $('#switch-list');
		let tilesSw = $('#switch-tiles');
		let imgSw   = $('#switch-img');

		let layoutSwitchFunction = function() {
			let state = $(this).hasClass('inactive');

			if (state) {
				let viewMode = $(this).data('viewMode');
				_Files.setViewMode(viewMode);

				_Entities.changeBooleanAttribute(listSw,  _Files.isViewModeActive('list'),  'List',   'List');
				_Entities.changeBooleanAttribute(tilesSw, _Files.isViewModeActive('tiles'), 'Tiles',  'Tiles');
				_Entities.changeBooleanAttribute(imgSw,   _Files.isViewModeActive('img'),   'Images', 'Images');

				_Files.displayFolderContents(id, parentId, nodePath, parents);
			}
		};

		listSw.on('click', layoutSwitchFunction);
		tilesSw.on('click', layoutSwitchFunction);
		imgSw.on('click', layoutSwitchFunction);
	},
	fileOrFolderCreationNotification: function (newFileOrFolder) {
		if ((_Files.currentWorkingDir === undefined || _Files.currentWorkingDir === null) && newFileOrFolder.parent === null) {
			_Files.appendFileOrFolder(newFileOrFolder);
		} else if ((_Files.currentWorkingDir !== undefined && _Files.currentWorkingDir !== null) && newFileOrFolder.parent && _Files.currentWorkingDir.id === newFileOrFolder.parent.id) {
			_Files.appendFileOrFolder(newFileOrFolder);
		}
	},
	appendFileOrFolder: function(d) {

		if (!d.isFile && !d.isFolder) return;

		StructrModel.createOrUpdateFromData(d, null, false);

		let size = d.isFolder ? (d.foldersCount + d.filesCount) : d.size;
		let icon = d.isFolder ? 'fa-folder' : _Icons.getFileIconClass(d);
		let name = d.name ? d.name : '[unnamed]';

		let listModeActive  = _Files.isViewModeActive('list');
		let tilesModeActive = _Files.isViewModeActive('tiles');
		let imageModeActive = _Files.isViewModeActive('img');

		let folderIconElement = (d.isMounted) ? `<span class="fa-stack"><i class="fa ${icon} fa-stack-2x"></i><i class="fa fa-plug fa-stack-1x"></i></span>` : `<i class="fa ${icon}"></i>`;

		if (listModeActive) {

			let tableBody = $('#files-table-body');

			$('#row' + d.id, tableBody).remove();

			let rowId = 'row' + d.id;
			tableBody.append(`<tr id="${rowId}"${(d.isThumbnail ? ' class="thumbnail"' : '')}></tr>`);
			let row = $('#' + rowId);

			if (d.isFolder) {

				row.append(`
					<td class="is-folder file-icon" data-target-id="${d.id}" data-parent-id="${d.parentId}">${folderIconElement}</td>
					<td>
						<div id="id_${d.id}" class="node folder flex items-center justify-between"><b class="name_ leading-8 truncate">${name}</b><div class="icons-container"></div></div>
					</td>
				`);

			} else {

				row.append(`
					<td class="file-icon"><a href="${d.path}" target="_blank"><i class="fa ${icon}"></i></a></td>
					<td>
						<div id="id_${d.id}" class="node file flex items-center justify-between"><b class="name_ leading-8 truncate">${name}</b><div class="icons-container"></div>
						<div class="progress"><div class="bar"><div class="indicator"><span class="part"></span>/<span class="size">${d.size}</span></div></div></div></div>
					</td>
				`);
			}

			row.append(`
				<td class="truncate id">${d.id}</td>
				<td class="size whitespace-nowrap">${d.isFolder ? size : _Files.formatBytes(size, 0)}</td>
				<td class="truncate">${d.type}${(d.isThumbnail ? ' thumbnail' : '')}${(d.isFile && d.contentType ? ` (${d.contentType})` : '')}</td>
				<td>${(d.owner ? (d.owner.name ? d.owner.name : '[unnamed]') : '')}</td>
			`);

			_Elements.enableContextMenuOnElement(row, d);

		} else if (tilesModeActive || imageModeActive) {

			let tileId = 'tile' + d.id;

			let tileClasses = ['tile'];
			if (d.isThumbnail)   { tileClasses.push('thumbnail'); }
			if (imageModeActive) { tileClasses.push('img-tile'); }

			_Files.folderContents.append(`<div id="${tileId}" class="${tileClasses.join(' ')}"></div>`);
			let tile = $('#' + tileId);

			if (d.isFolder) {

				tile.append(`
					<div id="id_${d.id}" class="node folder">
						<div class="is-folder file-icon" data-target-id="${d.id}" data-parent-id="${d.parentId}">${folderIconElement}</div>
						<b class="name_ abbr-ellipsis abbr-75pc">${name}</b>
					</div>
				`);

			} else {

				let thumbnailProperty = (tilesModeActive ? 'tnSmall' : 'tnMid');
				let iconOrThumbnail   = d.isImage && !d.isThumbnail && d[thumbnailProperty] ? `<img class="tn" src="${d[thumbnailProperty].path}">` : `<i class="fa ${icon}"></i>`;

				tile.append(`
					<div id="id_${d.id}" class="node file">
						<div class="file-icon"><a href="${d.path}" target="_blank">${iconOrThumbnail}</a></div>
						<b class="name_ abbr-ellipsis abbr-75pc">${name}</b>
						<div class="progress"><div class="bar"><div class="indicator"><span class="part"></span>/<span class="size">${size}</span></div></div></div><span class="id">${d.id}</span>
						<div class="icons-container"></div>
					</div>
				`);
			}

			_Elements.enableContextMenuOnElement(tile, d);
		}

		let div = Structr.node(d.id);

		if (!div || !div.length)
			return;

		_Entities.setMouseOver(div, true);

		let nameElement = div.children('b.name_');
		nameElement.attr('title', name);
		nameElement.off('click').on('click', (e) => {
			e.stopPropagation();
			_Entities.makeNameEditable(div);
		});

		div.on('remove', function() {
			div.closest('tr').remove();
		});

		if (d.isFolder) {
			_Files.handleFolder(div, d);
		} else {
			_Files.handleFile(div, d);
		}

		div.draggable({
			revert: 'invalid',
			containment: 'body',
			stack: '.jstree-node',
			appendTo: '#main',
			forceHelperSize: true,
			forcePlaceholderSize: true,
			distance: 5,
			cursorAt: { top: 8, left: 25 },
			zIndex: 99,
			stop: function(e, ui) {
				$(this).show();
				$(e.toElement).one('click', function(e) {
					e.stopImmediatePropagation();
				});
			},
			helper: function(event) {
				let helperEl = $(this);
				selectedElements = $('.node.selected');
				if (selectedElements.length > 1) {
					selectedElements.removeClass('selected');
					return $('<i class="node-helper ' + _Icons.getFullSpriteClass(_Icons.page_white_stack_icon) + '" />');
				}
				let hlp = helperEl.clone();
				hlp.find('.button').remove();
				return hlp;
			}
		});

		let iconsContainer = $('.icons-container', div);
		if (iconsContainer.length === 0) {
			_Entities.appendContextMenuIcon(div, d);
		} else {
			_Entities.appendContextMenuIcon(iconsContainer, d);
			_Entities.appendNewAccessControlIcon(iconsContainer, d, false);
		}

		if (d.isFile) {
			let dblclickHandler = (e) => {
				if ($('b.name_', div).length > 0) {
					_Files.editFile(d);
				}
			};

			if (div) {
				let node = div[0].closest('.node');
				node.removeEventListener('dblclick', dblclickHandler);
				node.addEventListener('dblclick', dblclickHandler);
			}
		}

		_Entities.makeSelectable(div);
	},
	handleFolder: function(div, d) {

		if (Structr.isModulePresent('cloud')) {
			div.append('<i title="Sync folder \'' + d.name + '\' to remote instance" class="push_icon button ' + _Icons.getFullSpriteClass(_Icons.push_file_icon) + '"></i>');
			div.children('.push_icon').on('click', function() {
				Structr.pushDialog(d.id, true);
				return false;
			});
		}

		// var delIcon = $('.delete_icon', div);
		// var newDelIcon = $('<i title="Delete folder \'' + d.name + '\'" class="delete_icon button ' + _Icons.getFullSpriteClass(_Icons.delete_icon) + '" />');
		//
		// if (delIcon && delIcon.length) {
		// 	delIcon.replaceWith(newDelIcon);
		// } else {
		// 	div.append(newDelIcon);
		// }
		// newDelIcon.on('click', function(e) {
		// 	e.stopPropagation();
		//
		// 	selectedElements = $('.node.selected');
		// 	var selectedCount = selectedElements.length;
		//
		// 	if (selectedCount > 1 && div.hasClass('selected')) {
		//
		// 		var files = [];
		//
		// 		$.each(selectedElements, function(i, el) {
		// 			files.push(Structr.entityFromElement(el));
		// 		});
		//
		// 		_Entities.deleteNodes(this, files, true, function() {
		// 			_Files.refreshTree();
		// 		});
		//
		// 	} else {
		// 		_Entities.deleteNode(this, d, true, function() {
		// 			_Files.refreshTree();
		// 		});
		// 	}
		// });

		div.droppable({
			accept: '.folder, .file, .image',
			greedy: true,
			hoverClass: 'nodeHover',
			tolerance: 'pointer',
			drop: function(e, ui) {

				e.preventDefault();
				e.stopPropagation();

				var self = $(this);
				var fileId = Structr.getId(ui.draggable);
				var folderId = Structr.getId(self);
				if (!(fileId === folderId)) {
					var nodeData = {};
					nodeData.id = fileId;

					if (selectedElements.length > 1) {

						$.each(selectedElements, function(i, fileEl) {
							var fileId = Structr.getId(fileEl);
							Command.setProperty(fileId, 'parentId', folderId, false, function() {
								$(ui.draggable).remove();
							});

						});
						selectedElements.length = 0;
					} else {
						Command.setProperty(fileId, 'parentId', folderId, false, function() {
							$(ui.draggable).remove();
						});
					}

					_Files.refreshTree();
				}

				return false;
			}
		});
	},
	handleFile: function(div, d) {

		if (Structr.isModulePresent('cloud') && !_Files.isViewModeActive('img')) {
			div.append('<i title="Sync file \'' + d.name + '\' to remote instance" class="push_icon button ' + _Icons.getFullSpriteClass(_Icons.push_file_icon) + '" />');
			div.children('.push_icon').on('click', function() {
				Structr.pushDialog(d.id, false);
				return false;
			});
		}
/*
		// if (_Files.isArchive(d)) {
		// 	div.append('<i class="unarchive_icon button ' + _Icons.getFullSpriteClass(_Icons.compress_icon) + '" />');
		// 	$('.unarchive_icon', div).on('click', function() {
		// 		_Files.unpackArchive(d);
		// 	});
		// }

		if (displayingFavorites === true) {

			// _Files.appendRemoveFavoriteIcon(div, d);

		} else {

			// div.append('<i title="Delete file \'' + d.name + '\'" class="delete_icon button ' + _Icons.getFullSpriteClass(_Icons.delete_icon) + '" />');
			// $('.delete_icon', div).on('click', function(e) {
			// 	e.stopPropagation();
			//
			// 	selectedElements = $('.node.selected');
			// 	var selectedCount = selectedElements.length;
			//
			// 	if (selectedCount > 1 && div.hasClass('selected')) {
			//
			// 		var files = [];
			//
			// 		$.each(selectedElements, function(i, el) {
			// 			files.push(Structr.entityFromElement(el));
			// 		});
			//
			// 		_Entities.deleteNodes(this, files, true, function() {
			// 			_Files.refreshTree();
			// 		});
			//
			// 	} else {
			// 		_Entities.deleteNode(this, d);
			// 	}
			// });
		}

		if (_Files.isMinificationTarget(d)) {
			_Files.appendMinificationDialogIcon(div, d);
		} else {
			// if (d.isImage && d.contentType !== 'text/svg' && !d.contentType.startsWith('image/svg')) {
			// 	_Files.appendEditImageIcon(div, d);
			// } else {
			// 	_Files.appendEditFileIcon(div, d);
			// }
		}

		// Structr.performModuleDependendAction(function() {
		// 	if (Structr.isModulePresent('csv') && Structr.isModulePresent('api-builder') && d.contentType === 'text/csv') {
		// 		_Files.appendCSVImportDialogIcon(div, d);
		// 	}
		// });
		//
		// Structr.performModuleDependendAction(function() {
		// 	if (Structr.isModulePresent('xml') && (d.contentType === 'text/xml' || d.contentType === 'application/xml')) {
		// 		_Files.appendXMLImportDialogIcon(div, d);
		// 	}
		// });
*/
	},
	unpackArchive: (d) => {

		$('#tempInfoBox .infoHeading, #tempInfoBox .infoMsg').empty();
		$('#tempInfoBox .closeButton').hide();
		$('#tempInfoBox .infoMsg').append(`<div class="flex items-center justify-center">${_Icons.getSvgIcon('waiting-spinner', 24, 24, 'mr-2')}<div>Unpacking Archive - please stand by...</div></div>`);
		$('#tempInfoBox .infoMsg').append('<p>Extraction will run in the background.<br>You can safely close this popup and work during this operation.<br>You will be notified when the extraction has finished.</p>');

		$.blockUI({
			message: $('#tempInfoBox'),
			css: Structr.defaultBlockUICss
		});

		var closed = false;
		window.setTimeout(function() {
			$('#tempInfoBox .closeButton').show().on('click', function () {
				closed = true;
				$.unblockUI({
					fadeOut: 25
				});
			});
		}, 500);

		Command.unarchive(d.id, _Files.currentWorkingDir ? _Files.currentWorkingDir.id : undefined, function (data) {
			if (data.success === true) {
				_Files.refreshTree();
				var message = "Extraction of '" + data.filename + "' finished successfully. ";
				if (closed) {
					new MessageBuilder().success(message).requiresConfirmation("Close").show();
				} else {
					$('#tempInfoBox .infoMsg').html('<i class="' + _Icons.getFullSpriteClass(_Icons.accept_icon) + '" /> ' + message);
				}

			} else {
				$('#tempInfoBox .infoMsg').html('<i class="' + _Icons.getFullSpriteClass(_Icons.error_icon) + '" /> Extraction failed');
			}
		});
	},
	// appendEditImageIcon: function(parent, image) {
	//
	// 	var viewIcon = $('.view_icon', parent);
	//
	// 	if (!(viewIcon && viewIcon.length)) {
	// 		parent.append('<i title="' + image.name + ' [' + image.id + ']" class="edit_icon button ' + _Icons.getFullSpriteClass(_Icons.edit_icon) + '" />');
	// 	}
	//
	// 	viewIcon = $('.edit_icon', parent);
	//
	// 	viewIcon.on('click', function(e) {
	// 		e.stopPropagation();
	// 		_Files.editImage(image);
	// 	});
	// },
	editImage: (image) => {
		let parent = Structr.node(image.id);
		Structr.dialog('' + image.name, function() {
			dialogMeta.show();
		}, function() {
			dialogMeta.show();
		});
		_Files.viewImage(image, $('#dialogBox .dialogText'));
	},
	viewImage: function(image, el) {
		dialogMeta.hide();

		el.append('<div class="image-editor-menubar ">'
			+ '<div><i class="fa fa-crop"></i><br>Crop</div>'
			+ '</div><div><img id="image-editor" class="orientation-' + image.orientation + '" src="' + image.path + '"></div>');

		var x,y,w,h;

		dialogBtn.children('#saveFile').remove();
		dialogBtn.children('#saveAndClose').remove();

		dialogBtn.append('<button id="saveFile" disabled="disabled" class="disabled">Save</button>');
		dialogBtn.append('<button id="saveAndClose" disabled="disabled" class="disabled">Save and close</button>');

		dialogSaveButton = $('#saveFile', dialogBtn);
		saveAndClose = $('#saveAndClose', dialogBtn);

		$('button#saveFile', dialogBtn).on('click', function(e) {
			e.preventDefault();
			e.stopPropagation();
			Command.createConvertedImage(image.id, Math.round(w), Math.round(h), null, Math.round(x), Math.round(y), function() {
				dialogSaveButton.prop("disabled", true).addClass('disabled');
				saveAndClose.prop("disabled", true).addClass('disabled');
			});
		});

		saveAndClose.on('click', function(e) {
			e.stopPropagation();
			dialogSaveButton.click();
			setTimeout(function() {
				dialogSaveButton.remove();
				saveAndClose.remove();
				dialogCancelButton.click();
			}, 500);
		});

		$('.fa-crop', el).on('click', function() {

			$('#image-editor').cropper({
				crop: function(e) {

					x = e.x, y = e.y, w = e.width, h = e.height;

					dialogSaveButton.prop("disabled", false).removeClass('disabled');
					saveAndClose.prop("disabled", false).removeClass('disabled');
				}
			});
		});
	},
	appendEditFileIcon: function(parent, file) {

		var editIcon = $('.edit_file_icon', parent);

		if (!(editIcon && editIcon.length)) {
			parent.append('<i title="Edit ' + file.name + ' [' + file.id + ']" class="edit_file_icon button ' + _Icons.getFullSpriteClass(_Icons.edit_icon) + '" />');
		}

		$(parent.children('.edit_file_icon')).on('click', function(e) {
			e.stopPropagation();
			_Files.editFile(file);
		});
	},
	editFile: (file) => {

		let parent = Structr.node(file.id);

		_Files.fileContents = {};

		selectedElements = $('.node.selected');
		if (selectedElements.length > 1 && parent.hasClass('selected')) {
			// selectedElements.removeClass('selected');
		} else {
			selectedElements = parent;
		}

		Structr.dialog('Edit files', function() {}, function() {}, ['popup-dialog-with-editor']);

		dialogText.append('<div id="files-tabs" class="files-tabs flex flex-col h-full"><ul></ul></div>');

		let filteredFileIds = [];
		if (selectedElements && selectedElements.length > 1 && parent.hasClass('selected')) {
			for (let el of selectedElements) {
				let modelObj = StructrModel.obj(Structr.getId(el));
				if (modelObj && !_Files.isMinificationTarget(modelObj) && modelObj.isFolder !== true) {
					filteredFileIds.push(modelObj.id);
				}
			}
		} else {
			let modelObj = StructrModel.obj(file.id);
			if (!modelObj) {
				modelObj = StructrModel.create(file);
			}
			if (modelObj && !_Files.isMinificationTarget(modelObj) && modelObj.isFolder !== true) {
				filteredFileIds.push(file.id);
			}
		}

		let filesTabs     = document.getElementById('files-tabs');
		let filesTabsUl   = filesTabs.querySelector('ul');
		let loadedEditors = 0;

		for (let uuid of filteredFileIds) {

			Command.get(uuid, 'id,name,contentType,isTemplate', (entity) => {

				loadedEditors++;

				let tab             = Structr.createSingleDOMElementFromHTML(`<li id="tab-${entity.id}" class="file-tab">${entity.name}</li>`);
				let editorContainer = Structr.createSingleDOMElementFromHTML(`<div id="content-tab-${entity.id}" class="content-tab-editor flex-grow flex"></div>`);

				filesTabsUl.appendChild(tab);
				filesTabs.appendChild(editorContainer);

				_Files.markFileEditorTabAsChanged(entity.id, _Files.fileHasUnsavedChanges[entity.id]);

				tab.addEventListener('click', (e) => {
					e.stopPropagation();

					// prevent activating the current tab
					if (!tab.classList.contains('active')) {

						// set all other tabs inactive and this one active
						for (let tab of filesTabsUl.querySelectorAll('li')) {
							tab.classList.remove('active');
						}
						tab.classList.add('active');

						// hide all editors and show this one
						for (let otherEditorContainer of filesTabs.querySelectorAll('div.content-tab-editor')) {
							otherEditorContainer.style.display = 'none';
						}
						editorContainer.style.display = 'block';

						// clear all other tabs before editing this one to ensure correct height
						for (let editor of filesTabs.querySelectorAll('.content-tab-editor')) {
							editor.innerHTML = '';
						}

						_Files.editFileWithMonaco(entity, $(editorContainer));
					}

					return false;
				});

				if (file.id === entity.id) {
					tab.click();
				}
			});
		}
	},
	markFileEditorTabAsChanged: (id, hasChanges) => {
		if (hasChanges) {
			$('#tab-' + id).addClass('has-changes');
		} else {
			$('#tab-' + id).removeClass('has-changes');
		}
	},
	editFileWithMonaco: async (file, element) => {

		// remove all buttons
		dialogBtn.children().remove();

		dialogBtn.html('<button class="closeButton">Close</button>');
		dialogBtn.append('<button id="saveFile" disabled="disabled" class="disabled">Save</button>');
		dialogBtn.append('<button id="saveAndClose" disabled="disabled" class="disabled">Save and close</button>');

		dialogCancelButton = $('.closeButton', dialogBox);
		dialogSaveButton   = $('#saveFile', dialogBtn);
		saveAndClose       = $('#saveAndClose', dialogBtn);

		element.append('<div class="editor h-full"></div><div id="template-preview"><textarea readonly></textarea></div>');

		let urlForFileAndPreview = Structr.viewRootUrl + file.id + '?' + Structr.getRequestParameterName('edit') + '=1';
		let fileResponse         = await fetch(urlForFileAndPreview);
		let data                 = await fileResponse.text();
		let initialText          = _Files.fileContents[file.id] || data;

		let fileMonacoConfig = {
			value: initialText,
			language: _Files.getLanguageForFile(file),
			lint: true,
			autocomplete: true,
			changeFn: (editor, entity) => {
				let currentText = editor.getValue();

				// Store current editor text
				_Files.fileContents[file.id] = currentText;

				_Files.fileHasUnsavedChanges[file.id] = (data !== currentText);

				_Files.markFileEditorTabAsChanged(file.id, _Files.fileHasUnsavedChanges[file.id]);

				if (data === currentText) {
					dialogSaveButton.prop("disabled", true).addClass('disabled');
					saveAndClose.prop("disabled", true).addClass('disabled');
				} else {
					dialogSaveButton.prop("disabled", false).removeClass('disabled');
					saveAndClose.prop("disabled", false).removeClass('disabled');
				}
			}
		};

		dialogMeta.html('<span class="editor-info"></span>');

		let monacoEditor = _Editors.getMonacoEditor(file, 'content', $('.editor', element), fileMonacoConfig);

		// for files only: install a listener for the ESC key which is only active if there is a suggestWidget visible
		monacoEditor.addCommand(monaco.KeyCode.Escape, () => {

			// set the structr-internal ignoreKeyUp so that the popup is not closed
			ignoreKeyUp = true;
			// blur the active element so that the popup is removed
			document.activeElement.blur();

		}, 'suggestWidgetVisible');

		let editorInfo = dialogMeta[0].querySelector('.editor-info');
		_Editors.appendEditorOptionsElement(editorInfo);
		_Files.appendTemplateConfig(editorInfo, monacoEditor, file, element, urlForFileAndPreview);

		_Editors.resizeVisibleEditors();

		fileMonacoConfig.changeFn(monacoEditor);
		monacoEditor.focus();

		dialogSaveButton.on('click', function(e) {

			e.preventDefault();
			e.stopPropagation();

			let newText = monacoEditor.getValue();
			if (data === newText) {
				return;
			}

			// update current value so we can check against it
			data = newText;
			fileMonacoConfig.changeFn(monacoEditor);

			let saveFileAction = (callback) => {
				_Files.updateTextFile(file, newText, callback);
				initialText = newText;
				dialogSaveButton.prop("disabled", true).addClass('disabled');
				saveAndClose.prop("disabled", true).addClass('disabled');
			};

			if ($('#isTemplate').is(':checked')) {

				_Entities.setProperty(file.id, 'isTemplate', false, false, () => {
					saveFileAction(() => {
						_Entities.setProperty(file.id, 'isTemplate', true, false, () => {
							let active = showPreviewCheckbox.is(':checked');
							if (active) {
								_Files.updateTemplatePreview(element, urlForFileAndPreview);
							}
						});
					});
				});

			} else {

				saveFileAction();
			}
		});

		let checkForUnsaved = () => {
			if ($('.file-tab.has-changes').length > 0) {
				return confirm('You have unsaved changes, really close without saving?');
			} else {
				return true;
			}
		};

		saveAndClose.on('click', function(e) {
			e.stopPropagation();
			dialogSaveButton.click();

			if (checkForUnsaved()) {
				setTimeout(function() {
					dialogSaveButton.remove();
					saveAndClose.remove();
					dialogCancelButton.click();
				}, 500);
			}
		});

		dialogCancelButton.on('click', (e) => {
			if (checkForUnsaved()) {
				e.stopPropagation();
				dialogText.empty();
				$.unblockUI({
					fadeOut: 25
				});

				dialogBtn.children(':not(.closeButton)').remove();

				Structr.focusSearchField();

				LSWrapper.removeItem(Structr.dialogDataKey);
			}
		});

		_Files.resize();
	},
	appendTemplateConfig: (element, editor, file, outerElement, urlForFileAndPreview) => {

		element.insertAdjacentHTML('beforeend', `
			<label for="isTemplate">Replace template expressions: <input id="isTemplate" type="checkbox" ${file.isTemplate ? 'checked' : ''}></label>
			<label for="showTemplatePreview">Show preview: <input id="showTemplatePreview" type="checkbox" ${file.isTemplate ? '' : 'disabled=disabled'}></label>
		`);

		let isTemplateCheckbox   = element.querySelector('#isTemplate');
		let showPreviewCheckbox  = element.querySelector('#showTemplatePreview');

		Structr.appendInfoTextToElement({
			text: "Expressions like <pre>Hello ${print(me.name)} !</pre> will be evaluated. To see a preview, tick the adjacent checkbox.",
			element: $(isTemplateCheckbox),
			insertAfter: true,
			css: {
				"margin-right": "4px"
			}
		});

		isTemplateCheckbox.addEventListener('change', () => {
			let active = isTemplateCheckbox.checked;
			_Entities.setProperty(file.id, 'isTemplate', active, false, function() {
				file.isTemplate = active;
				showPreviewCheckbox.disabled = !active;
			});
		});

		showPreviewCheckbox.addEventListener('change', () => {
			let active = showPreviewCheckbox.checked;
			if (active) {
				_Files.updateTemplatePreview(outerElement, urlForFileAndPreview);
			} else {
				let previewArea = $('#template-preview').hide();
				$('textarea', previewArea).val('');
				$('.editor', outerElement).width('inherit');
			}

			_Editors.resizeVisibleEditors();
		});
	},
	getLanguageForFile: (file) => {

		let language = file.contentType;

		if (language === 'application/javascript') {
			language = 'javascript';
		}

		if (!language) {
			if (file.name.endsWith('.css')) {
				language = 'css';
			} else if (file.name.endsWith('.js')) {
				language = 'javascript';
			} else {
				language = 'text';
			}
		}

		if (file.isTemplate) {
			language = 'javascript';
		}

		return language;
	},
	dialogSizeChanged: () => {
		_Editors.resizeVisibleEditors();
	},
	// appendMinificationDialogIcon: function(parent, file) {
	//
	// 	parent.append('<i title="Open minification dialog" class="minify_file_icon button ' + _Icons.getFullSpriteClass(_Icons.getMinificationIcon(file)) + '" />');
	// 	$('.minify_file_icon', parent).on('click', function(e) {
	// 		e.stopPropagation();
	//
	// 		_Minification.showMinificationDialog(file);
	// 	});
	// },
	// appendCSVImportDialogIcon: function(parent, file) {
	//
	// 	parent.append(' <i class="import_icon button ' + _Icons.getFullSpriteClass(_Icons.import_icon) + '" title="Import this CSV file" />');
	// 	$('.import_icon', parent).on('click', function() {
	// 		Importer.importCSVDialog(file, false);
	// 		return false;
	// 	});
	// },
	// appendXMLImportDialogIcon: function(parent, file) {
	//
	// 	parent.append(' <i class="import_icon button ' + _Icons.getFullSpriteClass(_Icons.import_icon) + '" title="Import this XML file" />');
	// 	$('.import_icon', parent).on('click', function() {
	// 		Importer.importXMLDialog(file, false);
	// 		return false;
	// 	});
	// },
//	appendRemoveFavoriteIcon: function (parent, file) {
//
//		parent.append('<i title="Remove from favorites" class="remove_favorite_icon button ' + _Icons.getFullSpriteClass(_Icons.star_delete_icon) + '" />');
//		$('.remove_favorite_icon', parent).on('click', function(e) {
//			e.stopPropagation();
//
//			Command.favorites('remove', file.id, function() {
//				parent.remove();
//			});
//		});
//	},
	displaySearchResultsForURL: async (url, searchString) => {

		let content = $('#folder-contents');
		$('#search-results').remove();
		content.append('<div id="search-results"></div>');

		let container = $('#search-results');

		let response = await fetch(url);

		if (response.ok) {
			let data = await response.json();

			if (!data.result || data.result.length === 0) {

				container.append('<h1>No results for "' + searchString + '"</h1>');
				container.append('<h2>Press ESC or click <a href="#filesystem" class="clear-results">here to clear</a> empty result list.</h2>');

				$('.clear-results', container).on('click', function() {
					_Files.clearSearch();
				});

			} else {

				container.append('<h1>' + data.result.length + ' result' + (data.result.length > 1 ? 's' : '') + ':</h1><table class="props"><thead><th class="_type">Type</th><th>Name</th><th>Size</th></thead><tbody></tbody></table>');
				container.append('<div id="search-results-details"></div>')

				let tbody = $('tbody', container);
				let detailsContainer = $('#search-results-details', container);

				for (let d of data.result) {

					tbody.append('<tr><td><i class="fa ' + _Icons.getFileIconClass(d) + '"></i> ' + d.type + (d.isFile && d.contentType ? ' (' + d.contentType + ')' : '') + '</td><td>' + d.name + '</td><td>' + d.size + '</td></tr>');

					let contextResponse = await fetch(Structr.rootUrl + 'files/' + d.id + '/getSearchContext', {
						method: 'POST',
						body: JSON.stringify({
							searchString: searchString,
							contextLength: 30
						})
					});

					if (contextResponse.ok) {

						let data = await contextResponse.json();

						if (data.result) {

							detailsContainer.append('<div class="search-result collapsed" id="results' + d.id + '"></div>');

							let div = $('#results' + d.id);

							div.append('<h2><i class="fa ' + _Icons.getFileIconClass(d) + '"></i> ' + d.name + '</h2>');
							div.append('<i class="toggle-height fa fa-expand"></i>').append('<i class="go-to-top fa fa-chevron-up"></i>');

							$('.toggle-height', div).on('click', function() {
								let icon = $(this);
								div.toggleClass('collapsed');
								icon.toggleClass('fa-expand');
								icon.toggleClass('fa-compress');
							});

							$('.go-to-top', div).on('click', function() {
								content.scrollTop(0);
							});

							for (let contextString of data.result.context) {

								for (let str of searchString.split(/[\s,;]/)) {
									contextString = contextString.replace(new RegExp('(' + str + ')', 'gi'), '<span class="highlight">$1</span>');
								}

								div.append('<div class="part">' + contextString + '</div>');
							}

							div.append('<div style="clear: both;"></div>');
						}
					}
				}
			}
		}
	},
	updateTextFile: function(file, text, callback) {
		if (text === "") {
			Command.chunk(file.id, 0, _Files.chunkSize, "", 1, callback);
		} else {
			var chunks = Math.ceil(text.length / _Files.chunkSize);
			for (var c = 0; c < chunks; c++) {
				var start = c * _Files.chunkSize;
				var end = (c + 1) * _Files.chunkSize;
				var chunk = utf8_to_b64(text.substring(start, end));
				Command.chunk(file.id, c, _Files.chunkSize, chunk, chunks, ((c+1 === chunks) ? callback : undefined));
			}
		}
	},
	updateTemplatePreview: async (element, url) => {

		let contentBox = $('.editor', element);
		contentBox.width('50%');

		let previewArea = $('#template-preview');
		previewArea.show();

		let response = await fetch(url.substr(0, url.indexOf('?')));
		let text     = await response.text();

		$('textarea', previewArea).val(text);
	},
	isArchive: function(file) {
		var contentType = file.contentType;
		var extension = file.name.substring(file.name.lastIndexOf('.') + 1);

		var archiveTypes = ['application/zip', 'application/x-tar', 'application/x-cpio', 'application/x-dump', 'application/x-java-archive', 'application/x-7z-compressed', 'application/x-ar', 'application/x-arj'];
		var archiveExtensions = ['zip', 'tar', 'cpio', 'dump', 'jar', '7z', 'ar', 'arj'];

		return isIn(contentType, archiveTypes) || isIn(extension, archiveExtensions);
	},
	isMinificationTarget: function(file) {
		let minifyTypes = [ 'MinifiedCssFile', 'MinifiedJavaScriptFile' ];
		return isIn(file.type, minifyTypes);
	},
	openMountDialog: function() {

		_Schema.getTypeInfo('Folder', function(typeInfo) {

			Structr.fetchHtmlTemplate('files/dialog.mount', {typeInfo: typeInfo}, function (html) {

				Structr.dialog('Mount Folder', function(){}, function(){});

				var elem = $(html);

				$('[data-info-text]', elem).each(function(i, el) {
					Structr.appendInfoTextToElement({
						element: $(el),
						text: $(el).data('info-text'),
						css: { marginLeft: "5px" }
					});
				});

				dialogText.append(elem);

				var mountButton = $('<button id="mount-folder">Mount</button>').on('click', function() {

					var mountConfig = {};
					$('.mount-option[type="text"]').each(function(i, el) {
						var val = $(el).val();
						if (val !== "") {
							mountConfig[$(el).data('attributeName')] = val;
						}
					});
					$('.mount-option[type="number"]').each(function(i, el) {
						var val = $(el).val();
						if (val !== "") {
							mountConfig[$(el).data('attributeName')] = parseInt(val);
						}
					});
					$('.mount-option[type="checkbox"]').each(function(i, el) {
						mountConfig[$(el).data('attributeName')] = $(el).prop('checked');
					});

					if (!mountConfig.name) {
						Structr.showAndHideInfoBoxMessage('Must supply name', 'warning', 2000);
					} else if (!mountConfig.mountTarget) {
						Structr.showAndHideInfoBoxMessage('Must supply mount target', 'warning', 2000);
					} else {
						mountConfig.type = 'Folder';
						mountConfig.parentId = _Files.currentWorkingDir ? _Files.currentWorkingDir.id : null;
						Command.create(mountConfig);

						dialogCancelButton.click();
					}
				});

				dialogBtn.prepend(mountButton);
			});
		});
	}
};