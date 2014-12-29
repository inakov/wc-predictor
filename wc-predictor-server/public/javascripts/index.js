var model = {
	statuses : {
			usable: {name: 'USABLE', smallIcon:'assets/images/clover-small.png'}, 
			danger: {name: 'DANGER', smallIcon:'assets/images/danger-small.png'}, 
			uncool: {name:'BAD', smallIcon:'assets/images/bad-small.png'}, 
			occupied: {name:'OCCUPIED', smallIcon:'assets/images/forbidden-small.png'},
	},
			
	getOtherStatuses : function(name) {
		var result = {};
		for(var s in model.statuses) {
			if(model.statuses[s].name != name) {
				result[s] = model.statuses[s];
			}
		}
		return result;
	}
};

var app = {
    initialize: function() {
        this.doInit();
    },
    doInit: function() {
    	$("#updateBtn").click(
    			function() {
    				app.update(5);
    			}
    		);
    	this.update(5);
    },
    
    update : function(floor) {
    	wc.loadFloor(floor, app.floorSuccess, app.floorError );
    },
    
    floorSuccess : function(data) {
    	
    	var floors = $('#floors');
    	floors.empty();
    	model.actions = {};
    	
    	for(var f = 0; f < data.floors.length; ++f) {
    		var floor = data.floors[f];
    		var floorDiv = $("<div/>").addClass("floor").text('floor ' + floor.id);
    		var floorTable = $("<table/>").attr("style", "width:100%;");
    		var floorTr = $("<tr/>");
    		floorTable.append(floorTr);
    		floorDiv.append(floorTable);
			for(var i = 0; i < floor.rooms.length; ++i) {
					var room = floor.rooms[i];
					var td = $("<td/>").attr("style", "width:50%");
					var div = $("<div/>")
						.text(room.name).attr('id', room.id);
					app.updateRoomDiv(room, div);
					
					td.append(div );
					floorTr.append(td);
			}
			floors.append(floorDiv);
			floors.append("<br/>");
    	}
    },
    
    floorError : function(xhr, status, exception) {
    	alert(status);
    },
    
    updateRoomDiv: function(room, div) {
		var status = room.currentStatus.statusType;
		div.removeClass();
		div.addClass(status.toLowerCase()).addClass("room");
//		div.find('.timer').remove();
		if(false && status != "USABLE" && status != "OCCUPIED") {
			var timer = $('<div/>').addClass('timer');
			timer.countdown(
				{until:+room.currentStatus.statusExpiration, compact:true, format:"M:s"}
			);
			div.append(timer);
		}
	
//		remove older actions table
		div.find('table a').each(
				function(i, a) {
					var id = '' + a.id	;
					delete model.actions[id];
				}
		);
		
		div.find('table').remove();
		var otherStatuses = model.getOtherStatuses(status);
		var actionsTable = $("<table/>").addClass("actions-table");
		
		var tr = $("<tr/>");
		for(os in otherStatuses) {
			var actionId = Date.now();
			var td = $("<td/>").addClass('action-box');
			var a = $('<a href="#" class="action-link" id="' + actionId + '"><img src="' + otherStatuses[os].smallIcon +'"/></a>');
			
			a.mousedown(
					function() {
						console.log('MOUSEDOWN ' + $(this).parent().attr('class'));
						$(this).parent().removeClass('action-box');
						$(this).parent().addClass('action-box-clicked');
					}
			);
			
			a.mouseup(
	    			function() {
	    				var id = '' + $(this).attr('id');
	    				var roomId = model.actions[id].roomId;
	    				var status = model.actions[id].status;
	    				wc.updateRoomStatus(roomId, status, 
	    						function(data) {
	    						var div = $('#' + data.id);
	    						app.updateRoomDiv(data, div);
	    				},
	    				function(xhr, s, exception) {
	    					alert(exception);
	    					
	    				}
	    				);
	    			}
	    	);
			
			td.append(a);
			tr.append(td);
			model.actions['' + actionId] = {status: otherStatuses[os].name, roomId : room.id};
			actionsTable.append(tr);
		}
		
		div.append(actionsTable);   	
    },
};	
