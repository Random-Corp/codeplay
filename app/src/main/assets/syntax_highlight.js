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
    $("#result").text(result);
};

const runCode = () => {
    code = $("#code").text().trim();
    if (code) {
        execScheme(code, updateResult);
    }
};

const showEdit = () => {
    $("#editCode").show();
    $("#doneButton").show();

    $("#code").hide();
    $("#editButton").hide();
};

const hideEdit = () => {
    $("#editCode").hide();
    $("#doneButton").hide();

    $("#code").show();
    $("#editButton").show();
};

const editCode = () => {
    code = $("#code").text();
    $("#editCode").val(code.trim());
    showEdit();
};

const updateCode = () => {
    code = $("#editCode").val();
    $("#code").text(code);
    refreshHighlight();

    hideEdit();
};
