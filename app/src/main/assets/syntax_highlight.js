// console.log(code);
// codeText = code.textAnnotations[0].description;
// skewer.log(codeText);

const loadCode = (codeText) => {
    block = document.getElementById("code");
    block.textContent = codeText;
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
        $("#errorMsg").hide();
        execScheme(code, updateResult);
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
