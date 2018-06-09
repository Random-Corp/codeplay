var schemeInterpretter = new BiwaScheme.Interpreter();

function execScheme(code, callback) {
    schemeInterpretter.evaluate(code, callback);
}