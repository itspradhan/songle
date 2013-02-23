<?php

// error_reporting(-1);
error_reporting( 0 );



  $track_full_name = $_FILES['file']['name'];
	$track_tmp_name = $_FILES['file']['tmp_name'];
	$track_size = $_FILES['file']['size'];
	$mime_track_type = $_FILES['file']['type'];



	$track_extension = strtolower( pathinfo( ' ' . $track_full_name, PATHINFO_EXTENSION ) );
	// $file_name = substr(pathinfo(' ' . $file_full_name, PATHINFO_FILENAME), 1);

	$track_destination_path = '/var/www/tracks/' . date( 'c' ) . ".{$track_extension}";

	// $track_destination_path = "/var/www/tracks/{$track_full_name}";

	$move_uploaded_file_state = move_uploaded_file( $track_tmp_name, $track_destination_path );

	if( !$move_uploaded_file_state )
	{



		die( '{ "error": "move file fail" }' );



	}

	chmod( $track_destination_path, 0777 );
// var_dump($move_uploaded_file_state);
// var_dump(is_dir('/var/www/tracks'));
// var_dump( "$track_full_name / $track_tmp_name / $track_size / $mime_track_type" );

	exec( "./echoprint-codegen {$track_destination_path}", $echoprint_codegen_output, $echoprnit_codegen_return_var );

	if( $echoprnit_codegen_return_var )
	{



		// die( 'code generation failed' );
		die( '{ "error": "code generation failed" }' );



	}

// var_dump(implode( $echoprint_codegen_output ));

	$track_profile_json_array = json_decode( $echoprint_codegen_output[ 1 ], TRUE );

// var_dump($track_profile_json_array);
// var_dump($track_profile_json_array[0][ 'code' ]);
// var_dump($track_profile_json_array[0][ 'metadata' ][ 'duration']);

	exec( "curl http://localhost:8080/ingest -d \"fp_code={$track_profile_json_array[ 'code' ]}&length={$track_profile_json_array[ 'metadata' ][ 'duration']}&codever=4.12\"", $echoprint_server_output, $echoprint_server_return_var );

	if( $echoprint_server_return_var )
	{



		// die( 'track ingest failed' );
		die( '{ "error": "track ingest failed" }' );


	}
// var_dump($echoprint_server_output);

	$track_id_json_array = json_decode( $echoprint_server_output[ 0 ], TRUE );

// var_dump($track_id_json_array);

	mysql_connect( 'localhost', 'root', 'password' ) or die( mysql_error() );

	mysql_query( "INSERT INTO Songle.track VALUES ( \"0\", \"{$track_id_json_array[ 'track_id' ]}\", \"{$track_profile_json_array[ 'metadata' ][ 'artist' ]}\", \"{$track_profile_json_array[ 'metadata' ][ 'title' ]}\" )" ) or die( mysql_error() );




	echo '{ state: "success" }';



?>
