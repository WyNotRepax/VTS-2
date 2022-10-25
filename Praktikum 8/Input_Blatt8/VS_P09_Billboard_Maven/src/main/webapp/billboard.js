globGetMethod = 0; /* 0: html; 1: xyz */


function setGetMethod (val) {
    globGetMethod = val;
} 

function $(id) {
    return document.getElementById(id);
}

function getXMLHttpRequest() {
    // XMLHttpRequest for Firefox, Opera, Safari
    if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
    }
    if (window.ActveObject) { // Internet Explorer
        try { // for IE new
            return new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e)  {  // for IE old
            try {
                return new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e)  {
                alert("Your browser does not support AJAX!");
                return null;
            }
        }
    }    
    return null;
} 

function getHttpRequest(url) {
    if (globGetMethod == 0)
        getHtmlHttpRequest(url);
    else
        /* xyz = JSON oder XML .... */
        getxyzHttpRequest(url);
}

function getHtmlHttpRequest(url) {
    var xmlhttp = getXMLHttpRequest(); 
    xmlhttp.open("GET", url, true);
    xmlhttp.onreadystatechange = function() {
        if(xmlhttp.readyState != 4) {
            $('posters').innerHTML = 'Seite wird geladen ...';
        }
        if(xmlhttp.readyState == 4 && xmlhttp.status == 200) {
            $('posters').innerHTML = xmlhttp.responseText;
        }
        $('timestamp').innerHTML = new Date().toString();
    };
    xmlhttp.send(null);
}

function getxyzHttpRequest(url, wait = false) {
    fetch(url + "?wait=" + wait).then(response=>{
        response.json().then(buildTable);
        getxyzHttpRequest(url,true);
    });
}


function postHttpRequest(url) {
    const inputElement = document.getElementById("contents")

    fetch(url + "?message=" + encodeURIComponent(inputElement.value),{method: "POST"});
}

function putHttpRequest(url, id) {
    let inputElement = $(`input_element_${id}`);
    fetch(url + `?message=${inputElement.value}&id=${id}`,{method: "PUT"});
}

function deleteHttpRequest(url, id) {
    fetch(url + `?id=${id}`,{method: "DELETE"});
}

function buildTable(object){
    console.log(object)
    let postersElement = $("posters");
    postersElement.innerHTML = "";
    let tableElement = document.createElement("table");
    tableElement.rules = "none";
    tableElement.cellSpacing = 4;
    tableElement.cellPadding = 5;
    tableElement.border = "1";
    
    for(let i = 0; i < object.size; i++){
        let post = object.billboard.filter(post => post.id === i)[0];
        if(post === undefined){
            post = {
                id: i,
                message: "<empty>",
                readonly: true
            }
        }
        let postElement = document.createElement("tr");
        tableElement.appendChild(postElement);
        
        let idElement = document.createElement("td");
        postElement.appendChild(idElement);
        idElement.innerText = post.id;
        
        let messageElement = document.createElement("td");
        postElement.appendChild(messageElement);
        let messageInputElement = document.createElement("input");
        messageElement.appendChild(messageInputElement);
        
        messageInputElement.type = "text"
        messageInputElement.size = 100;
        messageInputElement.minlength = 100;
        messageInputElement.maxlength = 100;
        messageInputElement.id = `input_element_${post.id}`;
        messageInputElement.value = post.message;
        messageInputElement.readOnly = post.readonly;
        
        let updateElement = document.createElement("td");
        postElement.appendChild(updateElement);
        let deleteElement = document.createElement("td");
        postElement.appendChild(deleteElement);
        if(!post.readonly){
            let updateButtonElement = document.createElement("button");
            updateElement.appendChild(updateButtonElement);
            updateButtonElement.innerText = "Update";
            updateButtonElement.addEventListener("click",()=>putHttpRequest("BillBoardServer",post.id));
            
            let deleteButtonElement = document.createElement("button");
            deleteElement.appendChild(deleteButtonElement);
            deleteButtonElement.addEventListener("click",()=>deleteHttpRequest("BillBoardServer",post.id));
            deleteButtonElement.innerText = "Delete";
        }
    }
    postersElement.appendChild(tableElement);
    
    
}