var preTarget;
var data;
var original;
var packagedata;

window.onload = function() {
	var tab = document.getElementById("myTable");
	original = readTable(tab);
	$('tbody tr').click(function(){
	    if (preTarget != undefined){
	        preTarget.removeClass("success");
	    }
	    preTarget=$(this).addClass("success");
	});
	$('th').click(function(){
	    if (this.innerHTML.indexOf("Feature")<0)
	        sortTable(this) ;
	});
	$('td a').click(function(){
	    var url=urlPrefix+"/"+this.getAttribute("package")+"/";
	    if (this.classList.contains('pull-right')){
	       url+=this.innerHTML+".html";
	    }
	    this.setAttribute("target","_blank");
        this.setAttribute("href",url);
	});
}

function readTable(tab) {
	var col = tab.rows[0].cells.length;
	var tb = new Array();
	for (var i = 0; i < tab.rows.length; i++) {
		var temp = new Array();
		for (var j = 0; j < col; j++) {
			if (j == 0) {
			    var link=tab.rows[i].cells[j].querySelector("a");
				temp[j] = link.getAttribute("package");
				temp[j + 1] = link.innerHTML;
			} else
				temp[j + 1] = tab.rows[i].cells[j].innerHTML;
		}
		tb[i] = temp;
	}
	return tb;
}

function readMainTable() {
	if (data == undefined) {
		var tab = document.getElementById("myTable");
		data = readTable(tab);
	}
	return data;
}

function getIndex(node) {
	if (node) {
		var index = 0;
		while (node = node.previousSibling) {
			if (node.nodeType == 1)
				index++;
		}
		return index;
	}
}

function getSort(dom){
    var sortFlag=dom.getAttribute("sort")=="up";
    if (sortFlag){
        dom.setAttribute("sort","dw");
    }else{
        dom.setAttribute("sort","up");
    }
    return sortFlag;
}

function sortArray(dom) {
	var sortIndex = getIndex(dom) + 1;
	var arr = readMainTable();
	var sortFlag=getSort(dom);

	for (var i = 0; i < arr.length; i++) {
		for (var j = 0; j < i; j++) {
			if (sortFlag) {
				if (parseFloat(arr[i][sortIndex]) < parseFloat(arr[j][sortIndex])) {
					var temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			} else {
				if (parseFloat(arr[i][sortIndex]) > parseFloat(arr[j][sortIndex])) {
					var temp = arr[i];
					arr[i] = arr[j];
					arr[j] = temp;
				}
			}
		}
	}
	return arr;
}

function sortTable(dom) {
	var arr = sortArray(dom);
	fillTable(arr);
}

function fillTable(arr) {
	var tab = document.getElementById("myTable");
	var col = tab.rows[0].cells.length;
	for (var i = 0; i < arr.length; i++) {
		for (var j = 0; j < col; j++) {
			if (j == 0) {
			    var td=tab.rows[i].cells[j];
			    var link=td.querySelector("a");
			    link.setAttribute("package",arr[i][j]);
				link.innerHTML = arr[i][j+1];
				if (arr[i][j] == arr[i][j + 1]) {
				    td.setAttribute("title",arr[i][j]);
				    td.parentNode.setAttribute("class","text-danger")
					link.setAttribute("class","text-danger");
				} else {
				    td.setAttribute("title",arr[i][j]+"."+arr[i][j+1]);
				    td.parentNode.classList.remove("text-danger")
                    link.setAttribute("class","pull-right");
				}
			} else
				tab.rows[i].cells[j].innerHTML = arr[i][j + 1];
		}
	}
}

function createTable(arr) {
	var table = document.createElement("table");
	table.setAttribute("border", "1");
	table.setAttribute("id", "created");
	var thead = createHeader();
	var tbody = createBody(arr);
	table.appendChild(thead);
	table.appendChild(tbody);
	document.getElementById("newTable").appendChild(table);
}

function createTR(arr) {
	var tr = document.createElement("tr");

	for (var j = 0; j < arr.length - 1; j++) {
		var td = document.createElement("td");
		if (j == 0) {
			td.innerHTML = arr[j];
			td.setAttribute("onclick", "selectRow(this)");
			td.setAttribute("id", "name");
			td.setAttribute("title", arr[j + 1]);
			if (arr[j] == arr[j + 1])
				tr.setAttribute("class", "package");
		} else {
			td.innerHTML = arr[j + 1];
		}
		tr.appendChild(td);
	}

	return tr;
}

function createBody(arrs) {
	var tbody = document.createElement("tbody");
	for (var i = 0; i < arrs.length; i++) {
		var tr = createTR(arrs[i]);
		tbody.appendChild(tr);
	}
	tbody.setAttribute("id", "tempTable");
	return tbody;
}

function createHeader(arr) {
	var thead = document.createElement("thead");
	var tr = document.createElement("tr");
	var orig = document.getElementById("thead");
	for (var j = 0; j < orig.rows[0].cells.length; j++) {
		var td = document.createElement("th");
		if (j > 0) {
			td.setAttribute("onclick", "sortTable(this)");
		}
		td.innerHTML = orig.rows[0].cells[j].innerHTML;
		tr.appendChild(td);
	}
	thead.appendChild(tr);
	return thead;
}

function getFilterArr(val) {
	var res = new Array();
	var index = 0;
	for (var i = 0; i < original.length; i++) {
		if (original[i][1].toLowerCase().indexOf(val.toLowerCase()) >= 0) {
			res[index++] = original[i];
		}
	}
	return res;
}

function filter() {
	removeCreated();
	var val = document.getElementById("filterValue").value;
	if (val != "") {
		var arr = getFilterArr(val);
		if (arr.length > 0)
			createTable(arr);
	} else {
		removeCreated();
	}
}

function showPackage() {
	removeCreated();
	var arr = getPackage();
	createTable(arr);
}

function getPackage() {
	if (packageData == undefined) {
		isMain = false;
		var tab = document.getElementById("myTable");
		var col = tab.rows[0].cells.length;
		var packageData = new Array();
		var index = 0;
		for (var i = 0; i < tab.rows.length; i++) {

			if (tab.rows[i].getAttribute("class") == "package") {
				var temp = new Array();
				for (var j = 0; j < col; j++) {
					if (j == 0) {
						temp[j] = tab.rows[i].cells[j].innerHTML;
						temp[j + 1] = tab.rows[i].cells[j]
								.getAttribute("title");
					} else
						temp[j + 1] = tab.rows[i].cells[j].innerHTML;
				}
				packageData[index++] = temp;
			}
		}
	}
	return packageData;
}

function removeCreated() {
	var table = document.getElementById("created");
	if (table != undefined) {
		table.parentNode.removeChild(table);
	}
}
function reset() {
	removeCreated();
	document.getElementById("filterValue").value = "";
	isMain = true;
	fillTable(original);
}