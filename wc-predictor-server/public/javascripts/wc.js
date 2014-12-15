var wc = {
		loadFloor : function(id, successCallback, errorCallback) {
			console.log("LOAD");
			$.ajax(
	    			{
	    				url:"/floors",
	    				dataType:"json",
	    				crossDomain:true,
	    				success: function(data, status, xhr) {
	    					successCallback(data);
	    				},
	    				error: function(xhr, status, exception) {
	    					errorCallback(xhr, status, exception);
	    				},
	    				timeout: 16000
	    			}
	    	);
		},
		
		updateRoomStatus : function(roomId, stat, successCallback, errorCallback) {
			var req = '{"status":"' + stat + '"}';
			console.log("REQUEST" + req);
			$.ajax(
	    			{
	    				url:"/rooms/" + roomId + "/status",
	    				dataType:"json",
	    				type: "POST",
	    				crossDomain:true,
	    				data: req,
	    				headers: {'Content-Type': 'application/json'},
	    				success: function(data, status, xhr) {
	    					console.log(data.name + " : " + data.currentStatus.statusType);
	    					successCallback(data);
	    				},
	    				error: function(xhr, status, exception) {
	    					errorCallback(xhr, status, exception);
	    				},
	    				timeout: 16000
	    			}
	    	);
		}
}