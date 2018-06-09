// console.log(code);
// codeText = code.textAnnotations[0].description;
// skewer.log(codeText);

const replaceBrace = (text) => {
    return text.replace(/\([ ]*/,"(").replace(/\)[ ]*/,")");
};

const loadCode = (codeText) => {
    let newCode = replaceBrace(codeText);
    block = document.getElementById("code");
    block.textContent = newCode;
    refreshHighlight();
};

const refreshHighlight = () => {
    block = document.getElementById("code");
    hljs.highlightBlock(block);
};

const updateResult = (result) => {
    if (jQuery.isEmptyObject(result)) {
        $("#result").show();
        $("#result").text(result);
    }
};

const runCode = (code) => {
    if (code) {
        $("#canvasContainer").hide();
        $("#errorMsg").hide();
        execScheme(code, handleResult);
    }
};

const showEdit = () => {
    $("#editCodeContainer").show();
    $("#doneButton").show();

    $("#codeContainer").hide();
    $("#editButton").hide();
};

const hideEdit = () => {
    $("#editCodeContainer").hide();
    $("#doneButton").hide();

    $("#codeContainer").show();
    $("#editButton").show();
};

const editCode = () => {
    code = $("#code").text();
    $("#editCodeText").val(code.trim());
    showEdit();
};

const updateCode = () => {
    code = $("#editCodeText").val();
    $("#code").text(code);
    refreshHighlight();

    hideEdit();
};

const saveCode = () => {
    let code = $("#code").text();
    let name = $("#snippet-name").val();
    var android = window.Android;
    android.saveSnippet(name, code);
};

const loadData = () => {
    // var data = ["test 1", "test 2", "test 3"];
    var android = window.Android;
    var snippets = android.loadSnippet();
    console.log(snippets);
    var data = JSON.parse(snippets);
    var options = '';
    $('#loadBody').html('');
    $('#LoadModalLoadButton').on("click", ()=>{ 
        console.log("----------click called----------")
        var features = [];
        $('#loadBody input[type="checkbox"]:checked').each(function() {
            console.log($(this).val());
            var name = $(this).val()
            var temp = '';
            $.each(data, function(hash,d){
                if (d.snippetName === name) {
                    temp = d.snippetCode;
                }
            });
            
            console.log(temp);
            if (temp != '') {
                features.push(temp);
            }
        });
        console.log(JSON.stringify(features));
        $('#code').text(features.join('\n'));
    });
    console.log(JSON.stringify(data));
    $.each(data, function(hash,name){
        console.log(JSON.stringify(name));
        options += '<input type="checkbox" name="checkbox-' + hash + '" id="checkbox-' + hash + '" value="' + name.snippetName + '" class="custom" />';
        options += '<label for="checkbox-' + hash + '">' + name.snippetName + '</label>';
        options += '</br>'
    });
    $('#loadBody').html(options);
};

const clearCode = () => {
    $('#code').text("");
};

const nukeTable = () => {
    var android = window.Android;
    android.deleteSnippets();
};
