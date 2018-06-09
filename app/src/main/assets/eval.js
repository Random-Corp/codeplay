const eval_it = () => {
        var show_error = function(e){
            console.log("Error: "+e.message);
            throw e;
        };
        var intp = new BiwaScheme.Interpreter(show_error);
        try{
            var ret = intp.evaluate(src);
            Console.puts(";=> " + BiwaScheme.to_write(ret));
        }
        catch(e){
            console.error(e);
        }
        dumper.dump_move(1);
    };
