/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function getClassificationTree(){

    var support_threshold = d3.select("[id=support_threshold_input]")[0][0].value;
    var confidence_threshold = d3.select("[id=confidence_threshold_input]")[0][0].value;
    var lift_threshold = d3.select("[id=lift_threshold_input]")[0][0].value;
    var unhighlightedArchs = [];
    var highlightedArchs = [];
    
    d3.selectAll("[class=dot]")[0].forEach(function(d){
        unhighlightedArchs.push(d.__data__.id);
    });
    d3.selectAll("[class=dot_clicked]")[0].forEach(function(d){
        highlightedArchs.push(d.__data__.id);
    });

    $.ajax({
        url: "DataAnalysisServlet",
        type: "POST",
        data: {ID: "buildClassificationTree", selected: JSON.stringify(highlightedArchs), unselected:JSON.stringify(unhighlightedArchs),
            supp:support_threshold,conf:confidence_threshold,lift:lift_threshold
        },

        async: false,
        success: function (data, textStatus, jqXHR){
            jsonObj_tree = JSON.parse(data);
        },
        complete: function (data){
            display_drivingFeatures(sortedDrivingFeatures,"lift");
        },
        error: function (jqXHR, textStatus, errorThrown)
        {alert("error");}
    });
    
    window.open('classificationTree.html');
}



