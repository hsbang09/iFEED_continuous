var sortedDrivingFeatures;
function getDrivingFeatures() {

    var support_threshold = d3.select("[id=support_threshold_input]")[0][0].value;
    var confidence_threshold = d3.select("[id=confidence_threshold_input]")[0][0].value;
    var lift_threshold = d3.select("[id=lift_threshold_input]")[0][0].value;
    var unhighlightedArchs = [];
    var highlightedArchs = [];
    
//   d3.selectAll("[class=dot_clicked]")[0].forEach(function(d){console.log(d.__data__);})
    d3.selectAll("[class=dot]")[0].forEach(function(d){
        unhighlightedArchs.push({inputs:d.__data__.inputs,objectives:d.__data__.objectives});
    });
    d3.selectAll("[class=dot_clicked]")[0].forEach(function(d){
        highlightedArchs.push({inputs:d.__data__.inputs,objectives:d.__data__.objectives});
    });

    $.ajax({
        url: "drivingFeatureServlet",
        type: "POST",
        data: {ID: "getDrivingFeatures", selected: JSON.stringify(highlightedArchs), unselected:JSON.stringify(unhighlightedArchs),
            supp:support_threshold,conf:confidence_threshold,lift:lift_threshold,
            candidateDrivingFeatures:JSON.stringify(candidateDrivingFeatures), candidateDrivingFeatures_names:JSON.stringify(candidateDrivingFeatures_names)},
        
        async: false,
        success: function (data, textStatus, jqXHR){
//            sortedDrivingFeatures = data;
            console.log(JSON.parse(data));
            sortedDrivingFeatures = JSON.parse(data);
        },
        complete: function (data){
            display_drivingFeatures(sortedDrivingFeatures,"lift");
        },
        error: function (jqXHR, textStatus, errorThrown)
        {alert("error");}
    });

//    sortedDrivingFeatures = sortDrivingFeatures("lift");
//    display_drivingFeatures(sortedDrivingFeatures,"lift");
//    loadDrivingFeaturesWindow(sortedDrivingFeatures);

}

function loadCandidateDrivingFeaturesWindow() {
    window.open('drivingFeatures.html');
}

function display_feature_option(){

    d3.select("[id=basicInfoBox_div]").select("g").remove();

    var archInfoBox = d3.select("[id=basicInfoBox_div]").append("g");
    archInfoBox.append("div")
            .attr("id","filter_title")
            .style("width","90%")
            .style("margin-top","10px")
            .style("margin-bottom","10px")
            .style("margin-left","2px")
            .style("float","left")
            .append("p")
            .text("Feature Setting")
            .style("font-size", "18px");
    var filterOptions = archInfoBox.append("div")
            .attr("id","filter_options")
            .style("width","100%")
            .style("height","100%")
            .style("float","left")
            .style("margin-bottom","10px");
    var filterOptions_inputs = d3.select("[id=filter_options]").append("div")
            .attr("id","filter_options_inputs")
            .style("width","100%")
            .style("height","380px")
            .style("float","left")
            .style("border","solid black 1px");
            
   //Slider Reference:    http://lokku.github.io/jquery-nstslider/     

   for (var i=0; i<jsonObj_scatterPlot[0].inputs.length;i++){
            var varName = jsonObj_scatterPlot[0].inputNames[i];
            var slider_input = filterOptions_inputs.append("div")
                .attr("id",varName + "_sliderbar_div")
                .style("width","100%")
                .style("height","60px")
                .style("float","left")
                .style("margin-left","15px");
            slider_input.append("div").style("width","100%").style("height","30px").style("float","left").text("include " + varName + ": ")
                .append("input").attr("type","checkBox").attr("id","include_" + varName);
      
            d3.select("[id=include_" + varName + "]").on("click",function(d){
                var includeInput = d3.select("[id=include_" + varName + "]")[0][0].checked;
                if(includeInput){
                    d3.select("[id=" + varName + "_sliderbar_div]").select("[class=nstSlider]").style("opacity",1);
                } else{
                    d3.select("[id=" + varName + "_sliderbar_div]").select("[class=nstSlider]").style("opacity",0.5);
                    }
                });
            
            var minInput = input_min[i];
            var maxInput = input_max[i];
            var slider_inputName = slider_input.append("div").attr("id","nstSlider_" + varName)
                .attr("class","nstSlider").attr("data-range_min",minInput).attr("data-range_max",maxInput)
                .attr("data-cur_min",minInput).attr("data-cur_max",maxInput).style("float","left").style("opacity",0.5);
            slider_inputName.append("div").attr("class","bar").style("float","left");
            slider_inputName.append("div").attr("class","leftGrip").style("float","left");
            slider_inputName.append("div").attr("class","rightGrip").style("float","left");
            slider_input.append("div").attr("class","rightLabel")
                .style("float","left")
                .style("margin-left","10px");
        }
    
    
//    d3.select("[id=NSAT_sliderbar_div]").append("input").attr("type","checkBox")
//    d3.select("[id=NSAT_sliderbar_div]").select("input")[0][0].checked
//    d3.select("[id=NSAT_sliderbar_div]").select("[class=rightLabel]").text()


    $('.nstSlider').nstSlider({
        "crossable_handles": false,
        "left_grip_selector": ".leftGrip",
        "right_grip_selector": ".rightGrip",
        "value_bar_selector": ".bar",
        "value_changed_callback": function(cause, leftValue, rightValue) {
            $(this).parent().find('.leftLabel').text(leftValue);
            $(this).parent().find('.rightLabel').text(rightValue);
        }
    });
    
    filterOptions.append("button")
            .attr("id","applyFilterButton_new")
            .attr("class","filterOptionButtons")
            .style("margin-left","6px")
            .style("margin-top","10px")
            .style("float","left")
            .text("Apply new filter");
    d3.select("[id=filter_options]").append("button")
            .attr("class","filterOptionButtons")
            .attr("id","applyFilterButton_add")
            .style("margin-left","6px")
            .style("margin-top","10px")
            .style("float","left")
            .text("Add to selection");
    d3.select("[id=filter_options]").append("button")
            .attr("id","applyFilterButton_within")
            .attr("class","filterOptionButtons")
            .style("margin-left","6px")
            .style("margin-top","10px")
            .style("float","left")
            .text("Search within selection");
    d3.select("[id=filter_options]").append("button")
            .attr("id","addCandidateDF")
            .attr("class","filterOptionButtons")
            .style("margin-left","6px")
            .style("margin-top","10px")
            .style("float","left")
            .text("add to candidate driving feature");
//
    d3.select("[id=applyFilterButton_add]").on("click",applyFilter_add);
    d3.select("[id=applyFilterButton_new]").on("click",applyFilter_new);
    d3.select("[id=applyFilterButton_within]").on("click",applyFilter_within);
    d3.select("[id=addCandidateDF]").on("click",addCandidateDF);
}


function applyFilter_new(){
    cancelDotSelections();
    applyFilter_add();
}

function applyFilter_within(){

//    d3.select("[id=NSAT_sliderbar_div]").select("input")[0][0].checked
//    d3.select("[id=NSAT_sliderbar_div]").select("[class=rightLabel]").text()
/** 
    var includeNPLANE = d3.select("[id=NPLANE_sliderbar_div]").select("input")[0][0].checked;
    var NPLANE_min_threshold = + d3.select("[id=NPLANE_sliderbar_div]").select("[class=leftLabel]").text();
    var NPLANE_max_threshold = + d3.select("[id=NPLANE_sliderbar_div]").select("[class=rightLabel]").text();
    var includeALT = d3.select("[id=ALT_sliderbar_div]").select("input")[0][0].checked;
    var ALT_min_threshold = + d3.select("[id=ALT_sliderbar_div]").select("[class=leftLabel]").text();
    var ALT_max_threshold = + d3.select("[id=ALT_sliderbar_div]").select("[class=rightLabel]").text();  */
    
    var include; var min_threshold; var max_threshold;
    include.toArray(); min_threshold.toArray(); max_threshold.toArray();
    
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    
    var includeSensorMassSpacingFactor = d3.select("[id=SensorMassSpacingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassSpacingFactor_min_threshold = + d3.select("[id=SensorMassSpacingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassSpacingFactor_max_threshold = + d3.select("[id=SensorMassSpacingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    
    var includeSensorReliabScalingFactor = d3.select("[id=SensorReliabScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorReliabScalingFactor_min_threshold = + d3.select("[id=SensorReliabScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorReliabScalingFactor_max_threshold = + d3.select("[id=SensorReliabScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    var includeSensorMassScalingFactor = d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("input")[0][0].checked;
    var SensorMassScalingFactor_min_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=leftLabel]").text();
    var SensorMassScalingFactor_max_threshold = + d3.select("[id=SensorMassScalingFactor_sliderbar_div]").select("[class=rightLabel]").text();
    
    
    include.push(includeSensorReliabScalingFactor); min_threshold.push(SensorReliabScalingFactor_min_threshold); max_threshold.push(SensorReliabScalingFactor_max_threshold);
    include.push(includeCompMassScalingFactor); min_threshold.push(CompMassScalingFactor_min_threshold); max_threshold.push(CompMassScalingFactor_max_threshold);
    include.push(includeCompMassSpacingFactor); min_threshold.push(CompMassSpacingFactor_min_threshold); max_threshold.push(CompMassSpacingFactor_max_threshold);
    include.push(includeCompReliabScalingFactor); min_threshold.push(CompReliabScalingFactor_min_threshold); max_threshold.push(CompReliabScalingFactor_max_threshold);
    include.push(includeDissimilarComponentPenalty); min_threshold.push(DissimilarComponentPenalty_min_threshold); max_threshold.push(DissimilarComponentPenalty_max_threshold);
    include.push(includeNS); min_threshold.push(NS_min_threshold); max_threshold.push(NS_max_threshold);
    include.push(includeNC); min_threshold.push(NC_min_threshold); max_threshold.push(NC_max_threshold);
    include.push(includeIbin_1); min_threshold.push(Ibin_1_min_threshold); max_threshold.push(Ibin_1_max_threshold);
    include.push(includeIbin_2); min_threshold.push(Ibin_2_min_threshold); max_threshold.push(Ibin_2_max_threshold);
    include.push(includeIbin_3); min_threshold.push(Ibin_3_min_threshold); max_threshold.push(Ibin_3_max_threshold);
    include.push(includeIbin_4); min_threshold.push(Ibin_4_min_threshold); max_threshold.push(Ibin_4_max_threshold);
    include.push(includeIbin_5); min_threshold.push(Ibin_5_min_threshold); max_threshold.push(Ibin_5_max_threshold);
    include.push(includeIbin_6); min_threshold.push(Ibin_6_min_threshold); max_threshold.push(Ibin_6_max_threshold);
    include.push(includeIbin_7); min_threshold.push(Ibin_7_min_threshold); max_threshold.push(Ibin_7_max_threshold);
    include.push(includeIbin_8); min_threshold.push(Ibin_8_min_threshold); max_threshold.push(Ibin_8_max_threshold);
    include.push(includeIbin_9); min_threshold.push(Ibin_9_min_threshold); max_threshold.push(Ibin_9_max_threshold);
    include.push(includeInat_1); min_threshold.push(Inat_1_min_threshold); max_threshold.push(Inat_1_max_threshold);
    include.push(includeInat_2); min_threshold.push(Inat_2_min_threshold); max_threshold.push(Inat_2_max_threshold);
    include.push(includeInat_3); min_threshold.push(Inat_3_min_threshold); max_threshold.push(Inat_3_max_threshold);
    
    for (var i=0; i<jsonObj_scatterPlot[0].inputs.length;i++){
        var varName = jsonObj_scatterPlot[0].inputNames[i];
        include[i] = d3.select("[id=" + varName + "_sliderbar_div]").select("input")[0][0].checked;
        min_threshold[i] = + d3.select("[id=" + varName + "_sliderbar_div]").select("[class=leftLabel]").text();
        max_threshold[i] = + d3.select("[id=" + varName + "_sliderbar_div]").select("[class=rightLabel]").text();
    }
    
    var clickedArchs = d3.selectAll("[class=dot_clicked]")[0].forEach(function (d) {
        var pass = true;
        for (var i=0; i<jsonObj_scatterPlot[0].inputs.length;i++){
            if(include[i]){
            var InputVal = d.__data__.inputs[i];
            if(InputVal >= min_threshold[i] && InputVal <= max_threshold[i]){
            } else {
                pass=false;
            }
        }}
/**        if(includeNSAT){
            var NSATVal = d.__data__.NSAT;
            if(NSATVal >= NSAT_min_threshold && NSATVal <= NSAT_max_threshold){
            } else {
                pass=false;
            }
        }
        if(includeNPLANE){
            var NPLANEVal = d.__data__.NPLANE;
            if(NPLANEVal >= NPLANE_min_threshold && NPLANEVal <= NPLANE_max_threshold){
            } else {
                pass=false;
            }
        }
*/

        if (pass===true){
            d3.select(d).attr("class", "dot_clicked")
                        .style("fill", "#0040FF");
        } else{
            d3.select(d).attr("class", "dot")
                    .style("fill", function (d) {
                            return "#000000";
                    });
        }
    });

    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs()); 
}


function applyFilter_add(){

//    d3.select("[id=NSAT_sliderbar_div]").select("input")[0][0].checked
//    d3.select("[id=NSAT_sliderbar_div]").select("[class=rightLabel]").text()

/** 
    var includeNPLANE = d3.select("[id=NPLANE_sliderbar_div]").select("input")[0][0].checked;
    var NPLANE_min_threshold = + d3.select("[id=NPLANE_sliderbar_div]").select("[class=leftLabel]").text();
    var NPLANE_max_threshold = + d3.select("[id=NPLANE_sliderbar_div]").select("[class=rightLabel]").text();
    var includeALT = d3.select("[id=ALT_sliderbar_div]").select("input")[0][0].checked;
    var ALT_min_threshold = + d3.select("[id=ALT_sliderbar_div]").select("[class=leftLabel]").text();
    var ALT_max_threshold = + d3.select("[id=ALT_sliderbar_div]").select("[class=rightLabel]").text();  */
    
    var include; var min_threshold; var max_threshold;
    include.toArray(); min_threshold.toArray(); max_threshold.toArray();
    
    include.push(includeSensorMassScalingFactor); min_threshold.push(SensorMassScalingFactor_min_threshold); max_threshold.push(SensorMassScalingFactor_max_threshold);
    include.push(includeSensorMassSpacingFactor); min_threshold.push(SensorMassSpacingFactor_min_threshold); max_threshold.push(SensorMassSpacingFactor_max_threshold);
    include.push(includeSensorReliabScalingFactor); min_threshold.push(SensorReliabScalingFactor_min_threshold); max_threshold.push(SensorReliabScalingFactor_max_threshold);
    include.push(includeCompMassScalingFactor); min_threshold.push(CompMassScalingFactor_min_threshold); max_threshold.push(CompMassScalingFactor_max_threshold);
    include.push(includeCompMassSpacingFactor); min_threshold.push(CompMassSpacingFactor_min_threshold); max_threshold.push(CompMassSpacingFactor_max_threshold);
    include.push(includeCompReliabScalingFactor); min_threshold.push(CompReliabScalingFactor_min_threshold); max_threshold.push(CompReliabScalingFactor_max_threshold);
    include.push(includeDissimilarComponentPenalty); min_threshold.push(DissimilarComponentPenalty_min_threshold); max_threshold.push(DissimilarComponentPenalty_max_threshold);
    include.push(includeNS); min_threshold.push(NS_min_threshold); max_threshold.push(NS_max_threshold);
    include.push(includeNC); min_threshold.push(NC_min_threshold); max_threshold.push(NC_max_threshold);
    include.push(includeIbin_1); min_threshold.push(Ibin_1_min_threshold); max_threshold.push(Ibin_1_max_threshold);
    include.push(includeIbin_2); min_threshold.push(Ibin_2_min_threshold); max_threshold.push(Ibin_2_max_threshold);
    include.push(includeIbin_3); min_threshold.push(Ibin_3_min_threshold); max_threshold.push(Ibin_3_max_threshold);
    include.push(includeIbin_4); min_threshold.push(Ibin_4_min_threshold); max_threshold.push(Ibin_4_max_threshold);
    include.push(includeIbin_5); min_threshold.push(Ibin_5_min_threshold); max_threshold.push(Ibin_5_max_threshold);
    include.push(includeIbin_6); min_threshold.push(Ibin_6_min_threshold); max_threshold.push(Ibin_6_max_threshold);
    include.push(includeIbin_7); min_threshold.push(Ibin_7_min_threshold); max_threshold.push(Ibin_7_max_threshold);
    include.push(includeIbin_8); min_threshold.push(Ibin_8_min_threshold); max_threshold.push(Ibin_8_max_threshold);
    include.push(includeIbin_9); min_threshold.push(Ibin_9_min_threshold); max_threshold.push(Ibin_9_max_threshold);
    include.push(includeInat_1); min_threshold.push(Inat_1_min_threshold); max_threshold.push(Inat_1_max_threshold);
    include.push(includeInat_2); min_threshold.push(Inat_2_min_threshold); max_threshold.push(Inat_2_max_threshold);
    include.push(includeInat_3); min_threshold.push(Inat_3_min_threshold); max_threshold.push(Inat_3_max_threshold);
    
    for (var i=0; i<jsonObj_scatterPlot[0].inputs.length;i++){
        var varName = jsonObj_scatterPlot[0].inputNames[i];
        include[i] = d3.select("[id=" + varName + "_sliderbar_div]").select("input")[0][0].checked;
        min_threshold[i] = + d3.select("[id=" + varName + "_sliderbar_div]").select("[class=leftLabel]").text();
        max_threshold[i] = + d3.select("[id=" + varName + "_sliderbar_div]").select("[class=rightLabel]").text();
    }
    
    var clickedArchs = d3.selectAll("[class=dot_clicked]")[0].forEach(function (d) {
        var pass = true;
        for (var i=0; i<jsonObj_scatterPlot[0].inputs.length;i++){
            if(include[i]){
            var InputVal = d.__data__.inputs[i];
            if(InputVal >= min_threshold[i] && InputVal <= max_threshold[i]){
            } else {
                pass=false;
            }
        }}
/**        if(includeNSAT){
            var NSATVal = d.__data__.NSAT;
            if(NSATVal >= NSAT_min_threshold && NSATVal <= NSAT_max_threshold){
            } else {
                pass=false;
            }
        }
        if(includeNPLANE){
            var NPLANEVal = d.__data__.NPLANE;
            if(NPLANEVal >= NPLANE_min_threshold && NPLANEVal <= NPLANE_max_threshold){
            } else {
                pass=false;
            }
        }
*/
        
        if (pass===true){
            d3.select(d).attr("class", "dot_clicked")
                        .style("fill", "#0040FF");
        }
    });
    
    d3.select("[id=numOfSelectedArchs_inputBox]").attr("value",numOfSelectedArchs());  
}


//  index for inputs + "-" + "exact:" + value
//  index for inputs + "-" + "min:" + value + "-" + "max:" + value

function generatePresetCandidateDF(){
    
    
//    input: SensorMassScalingFactor, SensorMassSpacingFactor, SensorReliabScalingFactor, CompMassScalingFactor, 
//           CompMassSpacingFactor, CompReliabScalingFactor, DissimilarComponentPenalty, NS, NC, Ibin_1, Ibin_2, 
//           Ibin_3, Ibin_4, Ibin_5, Ibin_6, Ibin_7, Ibin_8, Ibin_9, Inat_1, Inat_2, Inat_3

    var inputs = ["SensorMassScalingFactor", "SensorMassSpacingFactor", "SensorReliabScalingFactor", "CompMassScalingFactor",
                  "CompMassSpacingFactor", "CompReliabScalingFactor", "DissimilarComponentPenalty", "NS", "NC", "Ibin_1",
                  "Ibin_2", "Ibin_3", "Ibin_4", "Ibin_5", "Ibin_6", "Ibin_7", "Ibin_8", "Ibin_9", "Inat_1", "Inat_2", "Inat_3"];
    var input_max = [SensorMassScalingFactor_max, SensorMassSpacingFactor_max, SensorReliabScalingFactor_max, CompMassScalingFactor_max,
                     CompMassSpacingFactor_max, CompReliabScalingFactor_max, DissimilarComponentPenalty_max, NS_max, NC_max, Ibin_1_max,
                     Ibin_2_max, Ibin_3_max, Ibin_4_max, Ibin_5_max, Ibin_6_max, Ibin_7_max, Ibin_8_max, Ibin_9_max, Inat_1_max, Inat_2_max, Inat_3_max];
    var input_min = [SensorMassScalingFactor_min, SensorMassSpacingFactor_min, SensorReliabScalingFactor_min, CompMassScalingFactor_min,
                     CompMassSpacingFactor_min, CompReliabScalingFactor_min, DissimilarComponentPenalty_min, NS_min, NC_min, Ibin_1_min,
                     Ibin_2_min, Ibin_3_min, Ibin_4_min, Ibin_5_min, Ibin_6_min, Ibin_7_min, Ibin_8_min, Ibin_9_min, Inat_1_min, Inat_2_min, Inat_3_min];
    
    
    for (var i=0;i<inputs.length;i++){
        var mid = (input_max[i]-input_min[i])/2 + input_min[i];
        var mid_minus = (input_max[i]-input_min[i])/2 - 0.01 + input_min[i];
        var expression_low = inputs[i] + "-" + "min:" + input_min[i] + "-" + "max:" + mid_minus;
        var expression_high = inputs[i] + "-" + "min:" + mid + "-" + "max:" + input_max[i];
        candidateDrivingFeatures.push(expression_low);
        candidateDrivingFeatures_names.push(inputs[i] + " low");
        candidateDrivingFeatures.push(expression_high);
        candidateDrivingFeatures_names.push(inputs[i] + " high");
    }
    
    for(var i=0;i<inputs.length;i++){
        for(var j=i+1;j<inputs.length;j++){
            
            var var1_mid = (input_max[i]-input_min[i])/2 + input_min[i];
            var var1_mid_minus = (input_max[i]-input_min[i])/2 - 0.01 + input_min[i];
            var var1_expression_low = inputs[i] + "-" + "min:" + input_min[i] + "-" + "max:" + var1_mid_minus;
            var var1_expression_high = inputs[i] + "-" + "min:" + var1_mid + "-" + "max:" + input_max[i];
            var var2_mid = (input_max[j]-input_min[j])/2 +input_min[j];
            var var2_mid_minus = (input_max[j]-input_min[j])/2 - 0.01 + input_min[j];
            var var2_expression_low = inputs[j] + "-" + "min:" + input_min[j] + "-" + "max:" + var2_mid_minus;
            var var2_expression_high = inputs[j] + "-" + "min:" + var2_mid + "-" + "max:" + input_max[j];
            
            candidateDrivingFeatures.push(var1_expression_low + " and " + var2_expression_low);
            candidateDrivingFeatures_names.push(inputs[i] + " low and " + inputs[j] + " low");
            candidateDrivingFeatures.push(var1_expression_low + " and " + var2_expression_high);
            candidateDrivingFeatures_names.push(inputs[i] + " low and " + inputs[j] + " high");
            candidateDrivingFeatures.push(var1_expression_high + " and " + var2_expression_low);
            candidateDrivingFeatures_names.push(inputs[i] + " high and " + inputs[j] + " low");
            candidateDrivingFeatures.push(var1_expression_high + " and " + var2_expression_high);
            candidateDrivingFeatures_names.push(inputs[i] + " high and " + inputs[j] + " high");
        }
    }
    presetGenerated = true;
}

function addCandidateDF(){
    /** 
    var includeNPLANE = d3.select("[id=NPLANE_sliderbar_div]").select("input")[0][0].checked;
    var NPLANE_min_threshold = + d3.select("[id=NPLANE_sliderbar_div]").select("[class=leftLabel]").text();
    var NPLANE_max_threshold = + d3.select("[id=NPLANE_sliderbar_div]").select("[class=rightLabel]").text();
    var includeALT = d3.select("[id=ALT_sliderbar_div]").select("input")[0][0].checked;
    var ALT_min_threshold = + d3.select("[id=ALT_sliderbar_div]").select("[class=leftLabel]").text();
    var ALT_max_threshold = + d3.select("[id=ALT_sliderbar_div]").select("[class=rightLabel]").text();  */
    
    var include; var min_threshold; var max_threshold;
    include.toArray(); min_threshold.toArray(); max_threshold.toArray();
    
    include.push(includeSensorMassScalingFactor); min_threshold.push(SensorMassScalingFactor_min_threshold); max_threshold.push(SensorMassScalingFactor_max_threshold);
    include.push(includeSensorMassSpacingFactor); min_threshold.push(SensorMassSpacingFactor_min_threshold); max_threshold.push(SensorMassSpacingFactor_max_threshold);
    include.push(includeSensorReliabScalingFactor); min_threshold.push(SensorReliabScalingFactor_min_threshold); max_threshold.push(SensorReliabScalingFactor_max_threshold);
    include.push(includeCompMassScalingFactor); min_threshold.push(CompMassScalingFactor_min_threshold); max_threshold.push(CompMassScalingFactor_max_threshold);
    include.push(includeCompMassSpacingFactor); min_threshold.push(CompMassSpacingFactor_min_threshold); max_threshold.push(CompMassSpacingFactor_max_threshold);
    include.push(includeCompReliabScalingFactor); min_threshold.push(CompReliabScalingFactor_min_threshold); max_threshold.push(CompReliabScalingFactor_max_threshold);
    include.push(includeDissimilarComponentPenalty); min_threshold.push(DissimilarComponentPenalty_min_threshold); max_threshold.push(DissimilarComponentPenalty_max_threshold);
    include.push(includeNS); min_threshold.push(NS_min_threshold); max_threshold.push(NS_max_threshold);
    include.push(includeNC); min_threshold.push(NC_min_threshold); max_threshold.push(NC_max_threshold);
    include.push(includeIbin_1); min_threshold.push(Ibin_1_min_threshold); max_threshold.push(Ibin_1_max_threshold);
    include.push(includeIbin_2); min_threshold.push(Ibin_2_min_threshold); max_threshold.push(Ibin_2_max_threshold);
    include.push(includeIbin_3); min_threshold.push(Ibin_3_min_threshold); max_threshold.push(Ibin_3_max_threshold);
    include.push(includeIbin_4); min_threshold.push(Ibin_4_min_threshold); max_threshold.push(Ibin_4_max_threshold);
    include.push(includeIbin_5); min_threshold.push(Ibin_5_min_threshold); max_threshold.push(Ibin_5_max_threshold);
    include.push(includeIbin_6); min_threshold.push(Ibin_6_min_threshold); max_threshold.push(Ibin_6_max_threshold);
    include.push(includeIbin_7); min_threshold.push(Ibin_7_min_threshold); max_threshold.push(Ibin_7_max_threshold);
    include.push(includeIbin_8); min_threshold.push(Ibin_8_min_threshold); max_threshold.push(Ibin_8_max_threshold);
    include.push(includeIbin_9); min_threshold.push(Ibin_9_min_threshold); max_threshold.push(Ibin_9_max_threshold);
    include.push(includeInat_1); min_threshold.push(Inat_1_min_threshold); max_threshold.push(Inat_1_max_threshold);
    include.push(includeInat_2); min_threshold.push(Inat_2_min_threshold); max_threshold.push(Inat_2_max_threshold);
    include.push(includeInat_3); min_threshold.push(Inat_3_min_threshold); max_threshold.push(Inat_3_max_threshold);
    
    for (var i=0; i<jsonObj_scatterPlot[0].inputs.length;i++){
        var varName = jsonObj_scatterPlot[0].inputNames[i];
        include[i] = d3.select("[id=" + varName + "_sliderbar_div]").select("input")[0][0].checked;
        min_threshold[i] = + d3.select("[id=" + varName + "_sliderbar_div]").select("[class=leftLabel]").text();
        max_threshold[i] = + d3.select("[id=" + varName + "_sliderbar_div]").select("[class=rightLabel]").text();
    }
    
//  two options
//  index for inputs + "-" + "exact:" + value
//  index for inputs + "-" + "min:" + value + "-" + "max:" + value

    var expression="";
    var first=true;
    
    
    for (var i=0; i<jsonObj_scatterPlot[0].inputs.length;i++){
        var varName = jsonObj_scatterPlot[0].inputNames[i];
        if (i !== 0) {
            if(first){
                expression = expression + varName + "-";first=false;
            }else{
                expression = expression + " and " + varName + "-";
            }
        }
        if(include[i]){
            expression = expression + varName + "-";
            if(min_threshold[i] === max_threshold[i]){
                expression = expression + "exact:" + min_threshold[i];
            }
            } else {
                expression = expression + "min:" + min_threshold[i] + "-" + "max:" + max_threshold[i];
        }
    }
    
/**    if(includeNSAT){
        expression = expression + "NSAT" + "-";
        if(NSAT_min_threshold===NSAT_max_threshold){
            expression = expression + "exact:" + NSAT_min_threshold;
        }else{
            expression = expression + "min:" + NSAT_min_threshold + "-" + "max:" + NSAT_max_threshold;
        }
        first=false;
    }
    if(includeNPLANE){
        if(first){
            expression = expression + "NPLANE" + "-";first=false;
        }else{
            expression = expression + " and NPLANE" + "-";
        }
        
        if(NPLANE_min_threshold===NPLANE_max_threshold){
            expression = expression + "exact:" + NPLANE_min_threshold;
        }else{
            expression = expression + "min:" + NPLANE_min_threshold + "-" + "max:" + NPLANE_max_threshold;
        }
    } 
*/
    candidateDrivingFeatures.push(expression);
    candidateDrivingFeatures_names.push(expression);
}

               
var xScale_df;
var yScale_df;
var xAxis_df;
var yAxis_df;
var dfbar_width;
          
function display_drivingFeatures(source,sortby) {
    
    
    var size = source.length;
    var drivingFeatures = [];
    var drivingFeatureNames = [];
    var drivingFeatureExpressions = [];
    var i_drivingFeatures=0;
    var lifts=[];
    var supps=[];
    var conf1s=[];
    var conf2s=[];
    console.log(size);
    for (var i=0;i<size;i++){
        lifts.push(source[i].lift);
        supps.push(source[i].supp);
        conf1s.push(source[i].conf);
        conf2s.push(source[i].conf2);
        drivingFeatures.push(source[i]);
        drivingFeatureNames.push(source[i].name);
        drivingFeatureExpressions.push(source[i].expression);
    }

    var margin_df = {top: 20, right: 20, bottom: 10, left:65},
    width_df = 800 - 35 - margin_df.left - margin_df.right,
    height_df = 430 - 20 - margin_df.top - margin_df.bottom;

//    xScale_df = d3.scale.ordinal()
//            .rangeBands([0, width_df]);
    xScale_df = d3.scale.linear()
            .range([0, width_df]);
    yScale_df = d3.scale.linear().range([height_df-15, 0]);
    xScale_df.domain([0,drivingFeatures.length]);
    
    
    var minval;
    if(sortby==="lift"){
        minval = d3.min(lifts);
        yScale_df.domain([d3.min(lifts), d3.max(lifts)]);
    } else if(sortby==="supp"){
        minval = d3.min(supps);
        yScale_df.domain([d3.min(supps), d3.max(supps)]);
    }else if(sortby==="confave"){
        var min_tmp = (d3.min(conf1s) + d3.min(conf2s))/2;
        minval = min_tmp;
        var max_tmp = (d3.max(conf1s) + d3.max(conf2s))/2;
        yScale_df.domain([min_tmp, max_tmp]);
    }else if(sortby==="conf1"){
        minval = d3.min(conf1s);
        yScale_df.domain([d3.min(conf1s), d3.max(conf1s)]);
    }else if(sortby==="conf2"){
        minval = d3.min(conf2s);
        yScale_df.domain([d3.min(conf2s), d3.max(conf2s)]);
    }

    xAxis_df = d3.svg.axis()
            .scale(xScale_df)
            .orient("bottom")
            .tickFormat(function (d) { return ''; });
    yAxis_df = d3.svg.axis()
            .scale(yScale_df)
            .orient("left");

    var drivingFeaturesDisplayBox = d3.select("[id=drivingFeaturesAndSensitivityAnalysis_div]");

    d3.select("[id=basicInfoBox_div]").select("g").remove();
    var infoBox = d3.select("[id=basicInfoBox_div]")
            .append("g");


    var svg_df = infoBox.append("svg")
            .attr("width", width_df + margin_df.left + margin_df.right)
            .attr("height", height_df + margin_df.top + margin_df.bottom)
                .call(
                    d3.behavior.zoom()
                    .x(xScale_df)
                    .scaleExtent([1, 10])
                    .on("zoom", function (d) {

                        var svg = d3.select("[id=basicInfoBox_div]")
                                .select("svg");
                        var scale = d3.event.scale;

                        svg.select(".x.axis").call(xAxis_df);
                 
                        svg.selectAll("[class=bar]")
                                .attr("transform",function(d){
                                    var xCoord = xScale_df(d.id);
                                    return "translate(" + xCoord + "," + 0 + ")";
                                })
                                .attr("width", function(d){
                                    return dfbar_width*scale;
                                });

                        })
                    )
            .append("g")        
            .attr("transform", "translate(" + margin_df.left + "," + margin_df.top + ")");

////////////////////////////////////////////////////////
    // x-axis
    svg_df.append("g")
            .attr("class", "x axis")
            .attr("transform", "translate(0," + height_df + ")")
            .call(xAxis_df)
            .append("text")
            .attr("class", "label")
            .attr("x", width_df)
            .attr("y", -6)
            .style("text-anchor", "end");

    // y-axis
    svg_df.append("g")
            .attr("class", "y axis")
            .call(yAxis_df)
            .append("text")
            .attr("class","label")
            .attr("transform", "rotate(-90)")
            .attr("y",-60)
            .attr("x",-3)
            .attr("dy", ".71em")
            .style("text-anchor", "end")
            .text(function(d){
                if(sortby==="lift"){
                    return "Lift";
                } else if(sortby==="supp"){
                    return "Support";
                }else if(sortby==="confave"){
                    return "Average Confidence";
                }else if(sortby==="conf1"){
                    return "Confidence {feature}->{selection}";
                }else if(sortby==="conf2"){
                    return "Confidence {selection}->{feature}";
                }
            });

    var objects = svg_df.append("svg")
            .attr("class","dfbars_svg")
            .attr("width",width_df)
            .attr("height",height_df);

    //Create main 0,0 axis lines:
    objects.append("svg:line")
            .attr("class", "axisLine hAxisLine")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", width_df)
            .attr("y2", 0)
            .attr("transform", "translate(0," + (yScale_df(minval)) + ")");
    objects.append("svg:line")
            .attr("class", "axisLine vAxisLine")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", 0)
            .attr("y2", height_df)
            .attr("transform", "translate(" + (xScale_df(0)) + ",0)");
    /////////////////////////////////////////////////////////////////////////////////



    objects.selectAll(".bar")
            .data(drivingFeatures, function(d){return (d.id = i_drivingFeatures++);})
            .enter()
            .append("rect")
            .attr("class","bar")
            .attr("x", function(d) {
                return 0;
            })
            .attr("width", xScale_df(1))
            .attr("y", function(d) { 
                if(sortby==="lift"){
                    return yScale_df(d.lift); 
                } else if(sortby==="supp"){
                    return yScale_df(d.supp); 
                }else if(sortby==="confave"){
                    return yScale_df((d.conf+d.conf2)/2); 
                }else if(sortby==="conf1"){
                    return yScale_df(d.conf); 
                }else if(sortby==="conf2"){
                    return yScale_df(d.conf2); 
                }
            })
            .attr("height", function(d) { 
                if(sortby==="lift"){
                    return height_df - yScale_df(d.lift); 
                } else if(sortby==="supp"){
                    return height_df - yScale_df(d.supp); 
                }else if(sortby==="confave"){
                    return height_df - yScale_df((d.conf+d.conf2)/2); 
                }else if(sortby==="conf1"){
                    return height_df - yScale_df(d.conf); 
                }else if(sortby==="conf2"){
                    return height_df - yScale_df(d.conf2); 
                }
            })
            .attr("transform",function(d){
                var xCoord = xScale_df(d.id);
                return "translate(" + xCoord + "," + 0 + ")";
            })
            .style("fill", function(d,i){return color_drivingFeatures(drivingFeatureExpressions[i]);});
    dfbar_width = d3.select("[class=bar]").attr("width");

    var bars = d3.selectAll("[class=bar]")
   
                .on("mouseover",function(d){
                    var mouseLoc_x = d3.mouse(d3.select("[id=basicInfoBox_div]").select("[class=dfbars_svg]")[0][0])[0];
                    var mouseLoc_y = d3.mouse(d3.select("[id=basicInfoBox_div]").select("[class=dfbars_svg]")[0][0])[1];
                    var featureInfoLoc = {x:0,y:0};
                    var h_threshold = (width_df + margin_df.left + margin_df.right)*0.5;
                    var v_threshold = (height_df + margin_df.top + margin_df.bottom)*0.4;
                    var tooltip_width = 350;
                    var tooltip_height = 170;
                    if(mouseLoc_x >= h_threshold){
                        featureInfoLoc.x = -10 - tooltip_width;
                    } else{
                        featureInfoLoc.x = 10;
                    }
                    if(mouseLoc_y < v_threshold){
                        featureInfoLoc.y = 10;
                    } else{
                        featureInfoLoc.y = -10 -tooltip_height;
                    }
                    var svg_tmp = d3.select("[id=basicInfoBox_div]").select("[class=dfbars_svg]");
                    var featureInfoBox = svg_tmp.append("g")
                                                .attr("id","featureInfo_tooltip")
                                                .append("rect")
                                                .attr("id","featureInfo_box")
                                                .attr("transform", function(){
                                                    var x = mouseLoc_x + featureInfoLoc.x;
                                                    var y = mouseLoc_y + featureInfoLoc.y;
                                                    return "translate(" + x + "," + y + ")";
                                                })
                                                .attr("width",tooltip_width)
                                                .attr("height",tooltip_height)
                                                .style("fill","#4B4B4B")
                                                .style("opacity", 0.92);
                    var tmp= d.id;
                    var name = d.name;
                    var expression = d.expression;
                    var lift = d.lift;
                    var supp = d.supp;
                    var conf = d.conf;
                    var conf2 = d.conf2;

                    d3.selectAll("[class=bar]").filter(function(d){
                        if(d.id===tmp){
                            return true;
                        }else{
                            return false;
                        }
                    }).style("stroke-width",1.5)
                            .style("stroke","black");

                      

                    
                    var fo = d3.select("[id=basicInfoBox_div]").select("[class=dfbars_svg]")
                                    .append("g")
                                    .attr("id","foreignObject_tooltip")
                                    .append("foreignObject")
                                    .attr("x",function(){
                                        return mouseLoc_x + featureInfoLoc.x;
                                    })
                                    .attr("y",function(){
                                       return mouseLoc_y + featureInfoLoc.y; 
                                    })
                                    .attr({
                                        'width':tooltip_width,
                                        'height':tooltip_height  
                                    });
                                    
                    var fo_div = fo.append('xhtml:div')
                                            .attr({
                                                'class': 'tooltip'
                                            });
                    var textdiv = fo_div.selectAll("div")
                            .data([{name:name,supp:supp,conf:conf,conf2:conf2,lift:lift}])
                            .enter()
                            .append("div");
                          
//                    
                    textdiv.html(function(d){
                        var output= relabelDrivingFeatureName(d.name) + "<br> lift: " + d.lift.toFixed(4) + "<br> support: " + d.supp.toFixed(4) + 
                                "<br> conf {feature} -> {selection}: " + d.conf.toFixed(4) + "<br> conf2 {selection} -> {feature}: " + d.conf2.toFixed(4) +
                                "";
                        return output;
                    }).style("color", "#F7FF55");                         

           

                })
                .on("mouseout",function(d){
                    d3.select("[id=basicInfoBox_div]").selectAll("[id=featureInfo_tooltip]").remove();
                    d3.select("[id=basicInfoBox_div]").selectAll("[id=foreignObject_tooltip]").remove();
                    var tmp= d.id;
                    d3.selectAll("[class=bar]").filter(function(d){
                           if(d.id===tmp){
                               return true;
                           }else{
                               return false;
                           }
                       }).style("stroke-width",0)
                               .style("stroke","black");
                });

//
//    // draw legend
//    var legend_df = objects.selectAll(".legend")
//                    .data(color_drivingFeatures.domain())
//                    .enter().append("g")
//                    .attr("class", "legend")
//                    .attr("transform", function(d, i) { return "translate(0," + (i * 20) + ")"; });
//
//        // draw legend colored rectangles
//    legend_df.append("rect")
//            .attr("x", 655)
//            .attr("width", 18)
//            .attr("height", 18)
//            .style("fill", color_drivingFeatures);
//
//        // draw legend text
//    legend_df.append("text")
//            .attr("x", 655)
//            .attr("y", 9)
//            .attr("dy", ".35em")
//            .style("text-anchor", "end")
//            .text(function(d) { return d;});

    d3.select("[id=instrumentOptions]")
            .select("table").remove();
    
    d3.select("[id=dfsort_options]").on("change",dfsort);
}
                

function dfsort(){
    var sortby = d3.select("[id=dfsort_options]")[0][0].value;
//    "lift","supp","conf(ave)","conf(feature->selection)","conf(selection->feature)"

    display_drivingFeatures(sortedDrivingFeatures,sortby);
}
                

function openFeatureOptions(){
    display_feature_option();
}
                
function viewCandidateFeatures(){
    window.open('candidateFeatures.html');
}
   
   
