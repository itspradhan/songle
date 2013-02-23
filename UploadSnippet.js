$(function()
{
  



	// $( '#drop_box' ).mouseenter
	// (
	// 	function( Event )
	// 	{

	// 		$(this).css( 'border-color', 'black' );

	// 	}
	// ).mouseleave
	// (
	// 	function( Event )
	// 	{

	// 		$(this).css( 'border-color', 'grey' );

	// 	}
	// );



	var uploader = new plupload.Uploader({
		runtimes: 'html5,flash,html4',
		// runtimes : 'html5',
		// runtimes : 'html4',
		// runtimes : 'flash',
		url: '../searchTrack.php',
		drop_element: 'drop_box',
		browse_button: 'browse_file',
		container: 'drop_box',
		max_file_size: '32mb',
		multi_selection: false,
		browse_button_hover: 'pointer',
		browse_button_active: 'pointer',
		flash_swf_url: 'plupload/js/plupload.flash.swf',
		filters: 
		[
			{
				title: 'Audio files', extensions: 'mp3,aac,mov,m4a,wav,wma,amr,flac'
			}
		]
	});

	uploader.bind
	(
		'Init',
		function( Uploader, Parameter )
		{



			// alert( Uploader.id );
			// alert( Uploader.runtime );
			// alert( Parameter.runtime );


		}
	);

	uploader.init();



	uploader.bind
	(
		'Error',
		function( Uploader, Error )
		{



			$( '#drop_box' )
			.removeClass( 'loading' )
			.css( 'border-color', 'grey' );



			// alert(  "<div>Error: " + Error.code + ", Message: " + Error.message + ( Error.file ? ", File: " + Error.file.name : "" ) + "</div>");
			$( '#drop_box_message' )
			.fadeOut( 'fast' )
			.html( '<span id="error">Error: ' + Error.code + '<br />Message: ' + Error.message + ( Error.file ? '<br />File: ' + Error.file.name : '' ) + '</span>' )
			.fadeIn( 'fast' );



			Uploader.refresh(); // Reposition Flash/Silverlight



		}
	);

	uploader.bind
	(
		'FilesAdded',
		function( Uploader, FileArray )
		{



			$( '#drop_box_message' ).fadeOut( 'fast' );

			$( '#youtube_link' ).fadeOut( 'fast' );



		}
	);

	uploader.bind
	(
		'QueueChanged',
		function( Uploader )
		{



			Uploader.start();



		}
	);

	uploader.bind
	(
		'BeforeUpload',
		function( Uploader, File )
		{



			$( '#drop_box_message' )
			.fadeIn( 'fast' )
			.html( '' );



			$( '#drop_box' )
			.addClass( 'loading' )
			.css( 'border-color', 'black' );

			// $( '#loading' ).show();



		}
	);

	uploader.bind
	(
		'UploadProgress',
		function( Uploader, File )
		{

// alert(File.percent);

			$( '#drop_box_message' ).html( File.percent + '%' );
			

			if( File.percent == 100 )
			{



				$( '#drop_box_message' ).fadeOut( 'fast' );



			}



		}
	);

	uploader.bind
	(
		'FileUploaded',
		function( Uploader, File, Response )
		{



			$( '#drop_box' )
			.removeClass( 'loading' )
			.css( 'border-color', 'grey' );



			var response = Response.response;
// alert(response);		
			response = response.replace( String.fromCharCode(65279), '' );
// alert(response);
			var response = $.parseJSON( response );

			
			if( response.error )
			{



				$( '#drop_box_message' )
				.html( '<span id="error">' + response.error + '</span>' )
				.fadeIn( 'fast' );



			}
			else
			{



				$( '#drop_box_message' )
				.html( '<span id="title">' + response.title + '</span><span id="artist">by ' + response.artist + '</span>' )
				.fadeIn( 'fast' );
				
				$( '#youtube_link' )
				.attr( 'href', 'http://www.youtube.com/results?q=' + escape( response.title ) )
				.fadeIn( 'fast' );



			}



			Uploader.refresh();



		}
	);

});
