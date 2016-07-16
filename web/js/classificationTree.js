/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function getClassificationTree(){
              
    var support_threshold = d3.select("[id=support_threshold_input]")[0][0].value;
    var confidence_threshold = d3.select("[id=confidence_threshold_input]")[0][0].value;
    var lift_threshold = d3.select("[id=lift_threshold_input]")[0][0].value;

    var clickedArchs = d3.selectAll("[class=dot_clicked]");
    var numOfSelectedArchs = clickedArchs.size();
    var bitStrings = [];
    bitStrings.length = 0;

    for (var i = 0; i < numOfSelectedArchs; i++) {
        var tmpBitString = booleanArray2String(clickedArchs[0][i].__data__.archBitString);
        bitStrings.push(tmpBitString);
    }
    var drivingFeatures;
    
    
    $.ajax({
        url: "classificationTreeServlet",
        type: "POST",
        data: {ID: "initializeClassificationTree", bitStrings: JSON.stringify(bitStrings),supp:support_threshold,conf:confidence_threshold,lift:lift_threshold},
            async: false,
            success: function (data, textStatus, jqXHR)
            {
            },
            error: function (jqXHR, textStatus, errorThrown)
            {
                alert("error");
            }
    });
    

    for(var i=0;i<userDefFilters.length;i++){
        $.ajax({
            url: "classificationTreeServlet",
            type: "POST",
            data: {ID: "addUserDefFeatures", name: userDefFilters[i].name, expression: userDefFilters[i].expression},
                async: false,
                success: function (data, textStatus, jqXHR)
                {
                },
                error: function (jqXHR, textStatus, errorThrown)
                {
                    alert("error");
                }
        });
    }
    
    
    $.ajax({
        url: "classificationTreeServlet",
        type: "POST",
        data: {ID: "buildClassificationTree"},
            async: false,
            success: function (data, textStatus, jqXHR)
            {
                jsonObj_tree = JSON.parse(data);
            },
            error: function (jqXHR, textStatus, errorThrown)
            {
                alert("error");
            }
    });

    window.open('classificationTree.html');
}



