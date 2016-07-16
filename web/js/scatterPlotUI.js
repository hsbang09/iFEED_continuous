/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



function reset_drawing_scatterPlot() {
    d3.select("[id=scatterPlotFigure]").selectAll("svg").remove();
}

function draw_scatterPlot(source) {

    x_axis = d3.select("[id=axisOptions_x]")[0][0].value;
    y_axis = d3.select("[id=axisOptions_y]")[0][0].value;

    // setup x 
    xValue = function (d) {
        return get_objective_value(x_axis,d);
    }; // data -> value
    xScale = d3.scale.linear().range([0, width]); // value -> display
    //
    // don't want dots overlapping axis, so add in buffer to data domain 
    xBuffer = (d3.max(source, xValue) - d3.min(source, xValue)) * 0.05;
    xScale.domain([d3.min(source, xValue) - xBuffer, d3.max(source, xValue) + xBuffer]);

    xMap = function (d) {
        return xScale(xValue(d));
    }; // data -> display
    xAxis = d3.svg.axis().scale(xScale).orient("bottom");
//                                    .tickSize(-height);
//                                    .tickFormat(d3.format("s"));

    // setup y
    yValue = function (d) {
        return get_objective_value(y_axis,d);
    }; // data -> value
    yScale = d3.scale.linear().range([height, 0]); // value -> display

    yBuffer = (d3.max(source, yValue) - d3.min(source, yValue)) * 0.05;
    yScale.domain([d3.min(source, yValue) - yBuffer, d3.max(source, yValue) + yBuffer]);

    yMap = function (d) {
        return yScale(yValue(d));
    }; // data -> display
    yAxis = d3.svg.axis().scale(yScale).orient("left");
//                                .tickSize(-width);
//                                .tickFormat(d3.format("s"));


    svg = d3.select("[id=scatterPlotFigure]")
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
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
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");


//            var zoom = d3.behavior.zoom().x(function(){
//                console.log(xScale.domain());

    // x-axis
    svg.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height + ")")
            .call(xAxis)
            .append("text")
            .attr("class", "label")
            .attr("x", width)
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
            .attr("width", width)
            .attr("height", height);

    //Create main 0,0 axis lines:
    objects.append("svg:line")
            .attr("class", "axisLine hAxisLine")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", width)
            .attr("y2", 0)
            .attr("transform", "translate(0," + (yScale(0)) + ")");
    objects.append("svg:line")
            .attr("class", "axisLine vAxisLine")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", 0)
            .attr("y2", height)
            .attr("transform", "translate(" + (xScale(0)) + ",0)");

    var dots = objects.selectAll(".dot")
            .data(source)
            .enter().append("circle")
            .attr("class", "dot")
            .attr("r", 4)
            .attr("transform", function (d) {
                var xCoord = xMap(d);
                var yCoord = yMap(d);
                return "translate(" + xCoord + "," + yCoord + ")";
            })
//                .attr("cx", xMap)
//                .attr("cy", yMap)
            .style("fill", function (d) {
                return "#000000";
            });


    dots.on("mouseover", dot_mouseover)
            .on("mouseout", function (d) {
                if (d3.select(this).attr("class") === "dot_clicked") {
                } else {
                    d3.select(this).style("fill", "#000000");
                }
            });
    dots.on("click", dot_click);

//    if(testType==="1"){
//        d3.select("[id=getDrivingFeaturesSetting_div]").remove();
//        d3.select("[id=drivingFeaturesAndSensitivityAnalysis_div]").append("div")
//                .attr("id","getDrivingFeaturesSetting_div")
//                .append("button")
//                .attr("id","openFilterOptions")
//                .text("Open Filter Options");
//        d3.select("[id=scatterPlot_option]").remove();
//    }
//    else if(testType==="2"){
//        d3.select("[id=getClassificationTreeButton]").remove();
//    } else if(testType==="3"){
//    } else if(testType==="4"){
//        d3.select("[id=lift_threshold_input]")[0][0].value = 0;
//        d3.select("[id=getDrivingFeaturesSetting_div]").select("[id=getClassificationTreeButton]").remove();
//        d3.select("[id=scatterPlot_option]").remove();
//        d3.select("[id=dfsort_options]").remove();
//    }

//    if(testType==="4"){
//        d3.select("[id=getDrivingFeaturesButton]").on("click", getDrivingFeatures_automated);
//    } else{
        d3.select("[id=getDrivingFeaturesButton]").on("click", getDrivingFeatures);
//    }
    d3.select("[id=getClassificationTreeButton]").on("click",getClassificationTree);
    d3.select("[id=selectArchsWithinRangeButton]").on("click", selectArchsWithinRange);
    d3.select("[id=cancel_selection]").on("click",cancelDotSelections);
    d3.select("[id=hide_selection]").on("click",hideSelections);
    d3.select("[id=show_all_archs]").on("click",showAllArchs);
    d3.select("[id=openFeatureOptions]").on("click",openFeatureOptions);
    d3.select("[id=drivingFeaturesAndSensitivityAnalysis_div]").selectAll("options");
    d3.select("[id=numOfArchs_inputBox]").attr("value",numOfArchs());
    d3.select("[id=scatterPlot_option]").on("click",scatterPlot_option);
    d3.select("[id=viewCandidateFeatures]").on("click",viewCandidateFeatures);
    if (presetGenerated === false){
        generatePresetCandidateDF();
    }
    
//    d3.selectAll("[class=dot]")[0].forEach(function(d,i){
//        d3.select(d).attr("paretoRank",-1);
//    });

//    calculateParetoRanking();
//    drawParetoFront();
   
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
    d3.select("[id=instrumentOptions]")
            .select("table").remove();        
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
}

function hideSelections(){

    var clickedArchs = d3.selectAll("[class=dot_clicked]");

    clickedArchs.attr("class", "dot_hidden")
            .style("opacity", 0.04);
    d3.select("[id=instrumentOptions]")
            .select("table").remove();        
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
    d3.select("[id=instrumentOptions]")
            .select("table").remove();        
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


function scatterPlot_option(){ // three options: zoom, drag_selection, drag_deselection

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
                        .scaleExtent([0.4, 20])
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
//                        d3.selectAll("[class=dot_selected]").attr("class","dot"); 
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
                            
                            var x = get_objective_value(x_axis,d.__data__);
                            var y = get_objective_value(y_axis,d.__data__);
                            var xCoord = xScale(x);
                            var yCoord = yScale(y);

                            if( 
                                xCoord + margin.left>= b.x && xCoord + margin.left <= b.x+b.width && 
                                yCoord + margin.top >= b.y && yCoord + margin.top  <= b.y+b.height
                            ) {
                                d3.select(d).attr("class","dot_clicked")
                                        .style("fill", "#0040FF");      
                            }
                        });

                    }else{
                        dots = d3.selectAll("[class=dot_clicked]")[0].forEach(function(d,i){
                            var x = get_objective_value(x_axis,d.__data__);
                            var y = get_objective_value(y_axis,d.__data__);
                            var xCoord = xScale(x);
                            var yCoord = yScale(y);

                            if( 
                                xCoord + margin.left>= b.x && xCoord + margin.left <= b.x+b.width && 
                                yCoord + margin.top >= b.y && yCoord + margin.top  <= b.y+b.height
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


function get_objective_value(axis,d){
    if(axis==="avg_global"){return d.avg_global;}
    else if(axis==="avg_trop"){return d.avg_trop;}
    else if(axis==="avg_NH"){return d.avg_NH;}
    else if(axis==="avg_SH"){return d.avg_SH;}
    else if(axis==="avg_polar"){return d.avg_polar;}
    else if(axis==="avg_US"){return d.avg_US;}
    else if(axis==="max_global"){return d.max_global;}
    else if(axis==="max_trop"){return d.max_trop;}
    else if(axis==="max_NH"){return d.max_NH;}
    else if(axis==="max_SH"){return d.max_SH;}
    else if(axis==="max_polar"){return d.max_polar;}
    else if(axis==="max_US"){return d.max_US;}
}


//function drawParetoFront(){
//
//    var archsInParetoFront = d3.selectAll("[class=dot]")[0].filter(function(d){
//        if(d3.select(d).attr("paretoRank")=="1"){
//            return true;
//        }
//    });
//
//    var sortedScoreList = []; sortedScoreList.length=0;
//    var sortedArchList = []; sortedArchList.length=0;
//
//    var size = archsInParetoFront.length;
//
//    for(var i=0;i<size;i++){
//        var thisScore = archsInParetoFront[i].__data__.science;
//        var tmp = {
//                cost: archsInParetoFront[i].__data__.cost,
//                sci: archsInParetoFront[i].__data__.science
//        };
//
//        if(sortedScoreList.length==0){
//            sortedScoreList.push(thisScore);
//            sortedArchList.push(tmp);
//        }else{
//            var sortedLength = sortedScoreList.length;
//            for(var j=0;j<sortedLength;j++){
//                if(thisScore > sortedScoreList[j]){
//                    break;
//                }
//            }
//            sortedScoreList.splice(j, 0, thisScore);
//            sortedArchList.splice(j, 0, tmp);
//        }
//    }
//
//    var lines = []; lines.length=0;
//    for (var i=1;i<size;i++){
//        var line = {
//            x1: xScale(sortedArchList[i-1].sci),
//            x2: xScale(sortedArchList[i].sci),
//            y1: yScale(sortedArchList[i-1].cost),
//            y2: yScale(sortedArchList[i].cost) 
//        };
//        lines.push(line);
//    }
//
//    d3.select("[id=scatterPlotFigure]").select("svg")
//            .select("[class=objects]")
//            .selectAll("[class=paretoFrontier]")
//            .data(lines)
//            .enter()
//            .append("line")
//            .attr("class","paretoFrontier")
//            .attr("stroke-width", 1.5)
//            .attr("stroke", "#D00F0F")
//            .attr("x1",function(d){
//                return d.x1;
//            })
//            .attr("x2",function(d){
//                return d.x2;
//            })
//            .attr("y1",function(d){
//                return d.y1;
//            })
//            .attr("y2",function(d){
//                return d.y2;
//            });
//                                    var dots = objects.selectAll(".dot")
//                            .data(source)
//                            .enter().append("circle")

//var svg_tmp = d3.select("[id=scatterPlotFigure]").select("svg")
//                    objects = svg.append("svg")
//                            .attr("class", "objects")  

// 2var svgContainer = d3.select("body").append("svg")
// 3                                    .attr("width", 200)
// 4                                    .attr("height", 200);
// 5
// 6//Draw the line
// 7var circle = svgContainer.append("line")
// 8                         .attr("x1", 5)
// 9                         .attr("y1", 5)
//10                         .attr("x2", 50)
//11                         .attr("y2", 50);
//                    }
//}

//function calculateParetoRanking(){      
//    cancelDotSelections();
//
//    var archs = d3.selectAll("[class=dot]")[0].filter(function(d){
//        if(d3.select(d).attr("paretoRank")=="-1"){
//            return true;
//        }
//    });
//    if (archs.length==0){
//        return;
//    }
//
//
//    var rank=0;
//    archs = d3.selectAll("[class=dot]")[0];
//
//    while(archs.length > 0){
//
//        var numArchs = archs.length;
//        rank++;
//
//        if (rank>10){
//            break;
//        }
//
//        for (var i=0; i<numArchs; i++){
//            var non_dominated = true;
//            var thisArch = archs[i];
//
//            for (var j=0;j<numArchs;j++){
//                if (i==j) continue;
//                if (
//                    (thisArch.__data__.science <= archs[j].__data__.science &&
//                    thisArch.__data__.cost > archs[j].__data__.cost) || 
//                    (thisArch.__data__.science < archs[j].__data__.science &&
//                    thisArch.__data__.cost >= archs[j].__data__.cost) 
//                ){
//                    non_dominated = false;
//                }
//            }
//            if (non_dominated == true){
//                d3.select(thisArch).attr("paretoRank",""+rank);
//            } 
//        }
//        archs = d3.selectAll("[class=dot]")[0].filter(function(d){
//            if(d3.select(d).attr("paretoRank")=="-1"){
//                return true;
//            }
//        });
//    }
//
//}

//
//var cars;
//function getDrivingFeatures_automated(){
//    cancelDotSelections();
//    var clickedArchs = d3.selectAll("[class=dot_clicked]");
//    var unClickedArchs = d3.selectAll("[class=dot]");
//
//    var minCost = 0;
//    var maxCost = 5000;
//    var minScience = 0.15;
//    var maxScience = 1;
//
//    unClickedArchs.filter(function (d) {
//
//        var sci = d.science;
//        var cost = d.cost;
//
//        if (sci < minScience) {
//            return false;
//        } else if (sci > maxScience) {
//            return false;
//        } else if (cost < minCost) {
//            return false;
//        } else if (cost > maxCost) {
//            return false;
//        } else {
//            return true;
//        }
//    })
//            .attr("class", "dot_clicked");
//
//    clickedArchs.filter(function (d) {
//
//        var sci = d.science;
//        var cost = d.cost;
//
//        if (sci < minScience) {
//            return true;
//        } else if (sci > maxScience) {
//            return true;
//        } else if (cost < minCost) {
//            return true;
//        } else if (cost > maxCost) {
//            return true;
//        } else {
//            return false;
//        }
//    })
//            .attr("class", "dot");
////    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());
//    
//
//    var support_threshold = d3.select("[id=support_threshold_input]")[0][0].value;
//    var confidence_threshold = d3.select("[id=confidence_threshold_input]")[0][0].value;
//    var lift_threshold = d3.select("[id=lift_threshold_input]")[0][0].value;
//
//    clickedArchs = d3.selectAll("[class=dot_clicked]");
//    var numSelectedArchs = clickedArchs.size();
//
//    var bitStrings = [];
//    bitStrings.length = 0;
//
//    for (var i = 0; i < numSelectedArchs; i++) {
//        var tmpBitString = booleanArray2String(clickedArchs[0][i].__data__.archBitString);
//        bitStrings.push(tmpBitString);
//
//    }
//
//    $.ajax({
//        url: "drivingFeatureServlet",
//        type: "POST",
//        data: {ID: "automaticFeatureGeneration", bitStrings: JSON.stringify(bitStrings),supp:support_threshold,conf:confidence_threshold,lift:lift_threshold},
//        async: false,
//        success: function (data, textStatus, jqXHR)
//        {cars = JSON.parse(data);},
//        error: function (jqXHR, textStatus, errorThrown)
//        {alert("error");}
//    });
//    console.log(cars);
//    var aprioriDisplayWindow = window.open('drivingFeatures_apriori.html');
//    
//}