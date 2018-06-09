const errorHandler = (e) => {
    console.log("Error: "+e.message);
    $("#errorMsg").text(e.message);
    $("#errorMsg").show();
    $("#result").hide();
};

var intp = new BiwaScheme.Interpreter(errorHandler);

const createInterpretter = () => {
    intp = new BiwaScheme.Interpreter(errorHandler);
};

const execScheme = (code, callback) => {
    intp.evaluate(code, callback);
};
