/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function reset_drawing_scatterPlot() {
    d3.select("[id=scatterPlotFigure]").selectAll("svg").remove();
}

function draw_scatterPlot(source) {

    var x_axis = d3.select("[id=axisOptions_x]")[0][0].value;
    var y_axis = d3.select("[id=axisOptions_y]")[0][0].value;

    // setup x 
    var xValue = function (d) {
        return get_var_value(x_axis,d);
    }; // data -> value
    xScale = d3.scale.linear().range([0, scatterPlot_width]); // value -> display
    
    // don't want dots overlapping axis, so add in buffer to data domain 
    var xBuffer = (get_max_value(x_axis,source) - get_min_value(x_axis,source)) * 0.05;
    xScale.domain([get_min_value(x_axis,source) - xBuffer, get_max_value(x_axis,source) + xBuffer]);

    xMap = function (d) {
        return xScale(xValue(d));
    }; // data -> display
    xAxis = d3.svg.axis().scale(xScale).orient("bottom");

    // setup y
    yValue = function (d) {
        return get_var_value(y_axis,d);
    }; // data -> value
    yScale = d3.scale.linear().range([scatterPlot_height, 0]); // value -> display

    var yBuffer = (get_max_value(y_axis,source) - get_min_value(y_axis,source)) * 0.05;
    yScale.domain([get_min_value(y_axis,source) - yBuffer, get_max_value(y_axis,source) + yBuffer]);

    yMap = function (d) {
        return yScale(yValue(d));
    }; // data -> display
    yAxis = d3.svg.axis().scale(yScale).orient("left");

    var svg = d3.select("[id=scatterPlotFigure]")
        .append("svg")
        .attr("width", scatterPlot_width + scatterPlot_margin.left + scatterPlot_margin.right)
        .attr("height", scatterPlot_height + scatterPlot_margin.top + scatterPlot_margin.bottom)
        .call(
                d3.behavior.zoom()
                .x(xScale)
                .y(yScale)
                .scaleExtent([0.4, 20])
                .on("zoom", function (d) {

                    svg = d3.select("[id=scatterPlotFigure]")
                            .select("svg");

                    svg.select(".x.axis").call(xAxis);
                    svg.select(".y.axis").call(yAxis);

                    objects.select(".hAxisLine").attr("transform", "translate(0," + yScale(0) + ")");
                    objects.select(".vAxisLine").attr("transform", "translate(" + xScale(0) + ",0)");
                    //d3.event.translate[0]

                    svg.selectAll("[class=dot]")
                            .attr("transform", function (d) {
                                var xCoord = xMap(d);
                                var yCoord = yMap(d);
                                return "translate(" + xCoord + "," + yCoord + ")";
                            });
                    svg.selectAll("[class=dot_clicked]")
                            .attr("transform", function (d) {
                                var xCoord = xMap(d);
                                var yCoord = yMap(d);
                                return "translate(" + xCoord + "," + yCoord + ")";
                            });

                    svg.selectAll("[class=paretoFrontier]")
                            .attr("transform", function (d) {
                                return "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")";
                            })
                            .attr("stroke-width",1.5/d3.event.scale);

                    translate_tmp = d3.event.translate;
                    scale_tmp = d3.event.scale;

                })
                )
        .append("g")
        .attr("transform", "translate(" + scatterPlot_margin.left + "," + scatterPlot_margin.top + ")");

    // x-axis
    svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + scatterPlot_height + ")")
            .call(xAxis)
            .append("text")
            .attr("class", "label")
            .attr("x", scatterPlot_width)
            .attr("y", -6)
            .style("text-anchor", "end")
            .text(x_axis);

    // y-axis
    svg.append("g")
            .attr("class", "y axis")
            .call(yAxis)
            .append("text")
            .attr("class", "label")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text(y_axis);

    objects = svg.append("svg")
            .attr("class", "objects")
            .attr("width", scatterPlot_width)
            .attr("height", scatterPlot_height);

    //Create main 0,0 axis lines:
    objects.append("svg:line")
            .attr("class", "axisLine hAxisLine")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", scatterPlot_width)
            .attr("y2", 0)
            .attr("transform", "translate(0," + (yScale(0)) + ")");
    objects.append("svg:line")
            .attr("class", "axisLine vAxisLine")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", 0)
            .attr("y2", scatterPlot_height)
            .attr("transform", "translate(" + (xScale(0)) + ",0)");

    var dots = objects.selectAll(".dot")
            .data(source)
            .enter().append("circle")
            .attr("class", function(d){
                if(d.selected==true){
                    return "dot_clicked";
                }else{
                    return "dot";
                }
            })
            .attr("r", 4)
            .attr("transform", function (d) {
                var xCoord = xMap(d);
                var yCoord = yMap(d);
                return "translate(" + xCoord + "," + yCoord + ")";
            })
            .style("fill", function (d) {
                if (d.selected==true){
                    return "#0040FF";
                }else{
                    return "#000000";
                }
                
            });

    d3.selectAll(".dot")[0].forEach(function(d){
        d.__data__.selected=false;
    });

    dots.on("mouseover", dot_mouseover)
        .on("mouseout", function (d) {
            if (d3.select(this).attr("class") === "dot_clicked") {
            } else {
                d3.select(this).style("fill", "#000000");
            }
        });
    dots.on("click", dot_click);

    d3.select("[id=getDrivingFeaturesButton]").on("click", getDrivingFeatures);
    d3.select("[id=getClassificationTreeButton]").on("click",getClassificationTree);
//    d3.select("[id=selectArchsWithinRangeButton]").on("click", selectArchsWithinRange);
    d3.select("[id=cancel_selection]").on("click",cancelDotSelections);
    d3.select("[id=hide_selection]").on("click",hideSelections);
    d3.select("[id=show_all_archs]").on("click",showAllArchs);
    d3.select("[id=select_complement]").on("click",selectComplement);
    d3.select("[id=openFilterOptions]").on("click",openFilterOptions);
    d3.select("[id=numOfArchs_inputBox]").attr("value",numOfArchs());
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
    d3.select("[id=scatterPlot_option]").on("click",scatterPlot_toggle_option);
    d3.select("[id=viewCandidateFeatures]").on("click",viewCandidateFeatures);
    if (presetGenerated === false){
        generatePresetCandidateDF();
    }
    
    d3.select("[id=scatterPlot_option]").attr("class","zoom")
            .style("background-color", "#DFDFDF");
    
    d3.select("[id=axisOptions_x]").on("change",function(){

         d3.selectAll(".dot")[0].forEach(function(d){
             d.__data__.selected=false;
         });
         d3.selectAll(".dot_hidden")[0].forEach(function(d){
             d.__data__.selected=false;
         });
         d3.selectAll(".dot_clicked")[0].forEach(function(d){
             d.__data__.selected=true;
         });

         reset_drawing_scatterPlot();
         draw_scatterPlot(source);
    });
    d3.select("[id=axisOptions_y]").on("change",function(){

         d3.selectAll(".dot")[0].forEach(function(d){
             d.__data__.selected=false;
         });
         d3.selectAll(".dot_hidden")[0].forEach(function(d){
             d.__data__.selected=false;
         });
         d3.selectAll(".dot_clicked")[0].forEach(function(d){
             d.__data__.selected=true;
         });

         reset_drawing_scatterPlot();
         draw_scatterPlot(source);
    });
}


function selectArchsWithinRange() {

    var clickedArchs = d3.selectAll("[class=dot_clicked]");
    var unClickedArchs = d3.selectAll("[class=dot]");

    var minCost = d3.select("[id=selectArchsWithinRange_minCost]")[0][0].value;
    var maxCost = d3.select("[id=selectArchsWithinRange_maxCost]")[0][0].value;
    var minScience = d3.select("[id=selectArchsWithinRange_minScience]")[0][0].value;
    var maxScience = d3.select("[id=selectArchsWithinRange_maxScience]")[0][0].value;

    if (maxCost == "inf") {
        maxCost = 1000000000000;
    }

    unClickedArchs.filter(function (d) {

        var sci = d.science;
        var cost = d.cost;

        if (sci < minScience) {
            return false;
        } else if (sci > maxScience) {
            return false;
        } else if (cost < minCost) {
            return false;
        } else if (cost > maxCost) {
            return false;
        } else {
            return true;
        }
    })
            .attr("class", "dot_clicked")
            .style("fill", "#0040FF");

    clickedArchs.filter(function (d) {

        var sci = d.science;
        var cost = d.cost;

        if (sci < minScience) {
            return true;
        } else if (sci > maxScience) {
            return true;
        } else if (cost < minCost) {
            return true;
        } else if (cost > maxCost) {
            return true;
        } else {
            return false;
        }
    })
            .attr("class", "dot")
            .style("fill", function (d) {
                if (d.status == "added") {
                    return "#188836";
                } else if (d.status == "justAdded") {
                    return "#20FE5B";
                } else {
                    return "#000000";
                }
            });

    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
}

function cancelDotSelections(){

    var clickedArchs = d3.selectAll("[class=dot_clicked]");

    clickedArchs.attr("class", "dot")
            .style("fill", function (d) {
                    return "#000000";
            });
  
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
}

function hideSelections(){

    var clickedArchs = d3.selectAll("[class=dot_clicked]");

    clickedArchs.attr("class", "dot_hidden")
            .style("opacity", 0.04);     
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
    d3.select("[id=numOfArchs_inputBox]").attr("value",numOfArchs());
}
function showAllArchs(){

    var hiddenArchs = d3.selectAll("[class=dot_hidden]");

    hiddenArchs.attr("class", "dot")
            .style("fill", function (d) {
                    return "#000000";
            })
            .style("opacity",1);
    
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
    d3.select("[id=numOfArchs_inputBox]").attr("value",numOfArchs());
}
function selectComplement(){

    var unselected = d3.selectAll("[class=dot]");
    var selected = d3.selectAll("[class=dot_clicked]")

    selected.attr("class", "dot")
            .style("fill", function (d) {
                    return "#000000";
            });
    unselected.attr("class", "dot_clicked")
            .style("fill", "#0040FF");
    
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
    d3.select("[id=numOfArchs_inputBox]").attr("value",numOfArchs());
}



function dot_mouseover(d) {

    if (d3.select(this).attr("class") === "dot_clicked") {
    } else {
        d3.select(this).style("fill", "#D32020");
    }

    d3.select("[id=basicInfoBox_div]").select("g").remove();
    var archInfoBox = d3.select("[id=basicInfoBox_div]")
            .append("g");


    draw_archBasicInfoTable(d);

    d3.select("[id=instrumentOptions]")
            .select("table").remove();

}



function dot_click(d) {

    if (d3.select(this).attr("class") == "dot_clicked") {
        d3.select(this).attr("class", "dot")
                .style("fill", function (d) {
                        return "#000000";
                });

    } else {
        d3.select(this).attr("class", "dot_clicked")
                .style("fill", "#0040FF");

    }
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
}


function scatterPlot_toggle_option(){ // three options: zoom, drag_selection, drag_deselection

    var x_axis = d3.select("[id=axisOptions_x]")[0][0].value;
    var y_axis = d3.select("[id=axisOptions_y]")[0][0].value;
    
    if (d3.select("[id=scatterPlot_option]").attr("class")==="drag_deselection"){


        translate_tmp_local[0] = translate_tmp[0];
        translate_tmp_local[1] = translate_tmp[1];
        scale_tmp_local = scale_tmp;

        var svg_tmp =  d3.select("[id=scatterPlotFigure]")
            .select("svg")
            .on("mousedown",null)
            .on("mousemove",null)
            .on("mouseup",null);

        d3.select("[id=scatterPlot_option]").attr("class","zoom")
                .style("background-color", "#DFDFDF");

        d3.select("[id=scatterPlotFigure]")
            .select("svg")
            .call(
                d3.behavior.zoom()
                        .x(xScale)
                        .y(yScale)
                        .scaleExtent([0.4/scale_tmp_local, 20/scale_tmp_local])
                        .on("zoom", function (d) {

                            var svg = d3.select("[id=scatterPlotFigure]")
                                    .select("svg");

                            svg.select(".x.axis").call(xAxis);
                            svg.select(".y.axis").call(yAxis);

                            objects.select(".hAxisLine").attr("transform", "translate(0," + yScale(0) + ")");
                            objects.select(".vAxisLine").attr("transform", "translate(" + xScale(0) + ",0)");
                            //d3.event.translate[0]

                            svg.selectAll("[class=dot]")
                                    .attr("transform", function (d) {
                                        var xCoord = xMap(d);
                                        var yCoord = yMap(d);
                                        return "translate(" + xCoord + "," + yCoord + ")";
                                    });
                            svg.selectAll("[class=dot_hidden]")
                                    .attr("transform", function (d) {
                                        var xCoord = xMap(d);
                                        var yCoord = yMap(d);
                                        return "translate(" + xCoord + "," + yCoord + ")";
                                    });
                            svg.selectAll("[class=dot_clicked]")
                                    .attr("transform", function (d) {
                                        var xCoord = xMap(d);
                                        var yCoord = yMap(d);
                                        return "translate(" + xCoord + "," + yCoord + ")";
                                    });
                            svg.selectAll("[class=paretoFrontier]")
                                    .attr("transform", function (d) {
                                         var x = translate_tmp_local[0]*d3.event.scale + d3.event.translate[0];
                                         var y = translate_tmp_local[1]*d3.event.scale + d3.event.translate[1];
                                         var s = d3.event.scale*scale_tmp_local;
                                        return "translate(" + x +","+ y + ")scale(" + s + ")";
                                    })
                                     .attr("stroke-width",function(){
                                         return 1.5/(d3.event.scale*scale_tmp_local);
                                     });

                            translate_tmp[0] = d3.event.translate[0] + translate_tmp_local[0]*d3.event.scale;
                            translate_tmp[1] = d3.event.translate[1] + translate_tmp_local[1]*d3.event.scale;
                            scale_tmp = d3.event.scale*scale_tmp_local;

                        })       
            )  
    } else{
        var option;
        if(d3.select("[id=scatterPlot_option]").attr("class")=="zoom"){
            d3.select("[id=scatterPlot_option]").attr("class","drag_selection")
                .style("background-color", "#4BC41B");
            option = "selection";
        }else{
            d3.select("[id=scatterPlot_option]").attr("class","drag_deselection")
                .style("background-color", "#FA5F73");
            option = "deselection";
        }

        var svg_tmp =  d3.select("[id=scatterPlotFigure]")
            .select("svg")
            .call(d3.behavior.zoom().on("zoom",null));

        svg_tmp
            .on( "mousedown", function() {
                var p = d3.mouse( this);
                svg_tmp.append( "rect")
                        .attr({
                            rx      : 0,
                            ry      : 0,
                            class   : "selection",
                            x       : p[0],
                            y       : p[1],
                            width   : 0,
                            height  : 0,
                            x0      : p[0],
                            y0      : p[1]
                        })
                        .style("background-color", "#EEEEEE")
                        .style("opacity", 0.18);
            })
            .on( "mousemove", function() {

                var s = svg_tmp.select("rect.selection");
               if( !s.empty()) {
                    var p = d3.mouse( this);

                        b = {
                            x       : parseInt( s.attr("x"),10),
                            y       : parseInt( s.attr("y"), 10),
                            x0       : parseInt( s.attr("x0"),10),
                            y0       : parseInt( s.attr("y0"), 10),
                            width   : parseInt( s.attr("width"),10),
                            height  : parseInt( s.attr("height"), 10)
                        },
                        move = {
                            x : p[0] - b.x0,
                            y : p[1] - b.y0
                        };

                        if (move.x < 0){
                            b.x = b.x0 + move.x;

                        } else{
                            b.x = b.x0;
                        }
                        if (move.y < 0){
                            b.y = b.y0 + move.y;
                        } else {
                            b.y = b.y0;
                        }
                        b.width = Math.abs(move.x);
                        b.height = Math.abs(move.y);

                    s.attr( b);

                    var dots;  

                    if(option=="selection"){
                        dots = d3.selectAll("[class=dot]")[0].forEach(function(d,i){
                            
                            var x = get_var_value(x_axis,d.__data__);
                            var y = get_var_value(y_axis,d.__data__);
                            var xCoord = xScale(x);
                            var yCoord = yScale(y);

                            if( 
                                xCoord + scatterPlot_margin.left>= b.x && xCoord + scatterPlot_margin.left <= b.x+b.width && 
                                yCoord + scatterPlot_margin.top >= b.y && yCoord + scatterPlot_margin.top  <= b.y+b.height
                            ) {
                                d3.select(d).attr("class","dot_clicked")
                                        .style("fill", "#0040FF");      
                            }
                        });

                    }else{
                        dots = d3.selectAll("[class=dot_clicked]")[0].forEach(function(d,i){
                            var x = get_var_value(x_axis,d.__data__);
                            var y = get_var_value(y_axis,d.__data__);
                            var xCoord = xScale(x);
                            var yCoord = yScale(y);

                            if( 
                                xCoord + scatterPlot_margin.left>= b.x && xCoord + scatterPlot_margin.left <= b.x+b.width && 
                                yCoord + scatterPlot_margin.top >= b.y && yCoord + scatterPlot_margin.top  <= b.y+b.height
                            ) {
                                d3.select(d).attr("class","dot")
                                        .style("fill", function (d) {
                                                return "#000000";
                                        });      
                            }
                        });
                    }
                    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());

            }      
    })
    .on( "mouseup", function() {

        var svg_tmp =  d3.select("[id=scatterPlotFigure]")
            .select("svg");

           // remove selection frame
        svg_tmp.selectAll( "rect.selection").remove();
    });
    }               
}





function get_max_value(varName,d){
    /* For a given variable, returns the minimum value.
     * 
     * Inputs
     *      d: The dataset containing all datapoints.
    */
    var array =[];
    var index = find_var_index(varName);
    
    if (inputNames.indexOf(varName) > -1){
        // variable is input
        for (var i=0;i<d.length;i++){
            // Store all the values of the given input to an array
            array.push(d[i].inputs[index]);
        }
    }else{
        // variable is output
        for (var i=0;i<d.length;i++){
            // Store all the values of the given output to an array
            array.push(d[i].outputs[index]);
        }
    }
    return Math.max.apply(Math, array);
}




function get_min_value(varName,d){
    /* For a given variable, returns the minimum value.
     * 
     * Inputs
     *      d: The dataset containing all datapoints.
    */
    
    var array =[];
    var index = find_var_index(varName);
    
    if (inputNames.indexOf(varName) > -1){
        // variable is input
        for (var i=0;i<d.length;i++){
            // Store all the values of the given input to an array
            array.push(d[i].inputs[index]);
        }
    }else{
        // variable is output
        for (var i=0;i<d.length;i++){
            // Store all the values of the given output to an array
            array.push(d[i].outputs[index]);
        }
    }
    return Math.min.apply(Math, array);
}


function find_var_index(varName){
    // Iterates over all outputs and returns the index of the output whose name matches with the name given as an argument.
    var ind = 0;
    if (inputNames.indexOf(varName) > -1){
        // variable is an input
        for(var i=0;i<inputNames.length;i++){
            if (varName==inputNames[i]){
                ind = i;
                break;
            }
        }
    }else{
        // variable is an output
        for(var i=0;i<outputNames.length;i++){
            if (varName==outputNames[i]){
                ind = i;
                break;
            }
        }
    }
    return ind;
}

function get_var_value(axis,d){
    /* Returns the value of the variable for a single point data d.
     * 
     *  Inputs:
     *      axis: The name of an output variable
     *      d: Single point solution containing all variable names and their corresponding values 
    */
    
    ind = find_var_index(axis);
    if (inputNames.indexOf(axis) > -1){
        return d.inputs[ind];
    }else{
        return d.outputs[ind];
    }
}

