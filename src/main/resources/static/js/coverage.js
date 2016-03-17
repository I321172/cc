var preTarget;
var data;

window.onload = function() {
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
	filter();
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

function filter() {
	var val = $("#filterValue").val();
	var count=0;
	if (val != "") {
	    $('tr.hide').removeClass('hide');
	    $('a[package]').each(function(){
	       if( $(this).text().indexOf(val)<0&&$(this).attr("package").indexOf(val)<0){
	           $(this).parents('tr').addClass('hide');
	       }else{
	           count++;
	       }
	   });
	} 
	if (count==0){
	    $("#filterValue").val('No Result!')
	}
}

function list() {
    window.location.reload();
}