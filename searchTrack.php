<?php

// error_reporting(-1);
error_reporting( 0 );



  $snippet_full_name = $_FILES['file']['name'];
	$snippet_tmp_name = $_FILES['file']['tmp_name'];
	$snippet_size = $_FILES['file']['size'];
	$mime_snippet_type = $_FILES['file']['type'];


	$snippet_extension = strtolower( pathinfo( ' ' . $snippet_full_name, PATHINFO_EXTENSION ) );
	// $file_name = substr(pathinfo(' ' . $file_full_name, PATHINFO_FILENAME), 1);

	$snippet_destination_path = '/var/www/snippets/' . date( 'c' ) . ".{$snippet_extension}";

	$move_uploaded_file_state = move_uploaded_file( $snippet_tmp_name, $snippet_destination_path );

	if( !$move_uploaded_file_state )
	{



		// die( 'move file fail' );
		die( '{ "error": "move file fail" }' );


	}

	chmod( $snippet_destination_path, 0777 );
// var_dump($move_uploaded_file_state);
// var_dump(is_dir('/var/www/snippets'));
// var_dump( "$snippet_full_name / $snippet_tmp_name / $snippet_size / $mime_snippet_type" );

	exec( "./echoprint-codegen {$snippet_destination_path}", $echoprint_codegen_output, $echoprnit_codegen_return_var );

	if( $echoprnit_codegen_return_var )
	{



		// die( 'code generation failed' );
		die( '{ "error": "code generation failed" }' );



	}

// var_dump(implode( $echoprint_codegen_output ));

	$snippet_profile_json_array = json_decode( $echoprint_codegen_output[ 1 ], TRUE );

// var_dump($snippet_profile_json_array);
// var_dump($snippet_profile_json_array[0][ 'code' ]);
// var_dump($snippet_profile_json_array[0][ 'metadata' ][ 'duration']);

	exec( "curl http://localhost:8080/query -d \"fp_code={$snippet_profile_json_array[ 'code' ]}\"", $echoprint_server_output, $echoprint_server_return_var );

	if( $echoprint_server_return_var )
	{



		// die( 'query failed' );
		die( '{ "error": "query failed" }' );


	}
	
// var_dump($echoprint_server_output);

	$snippet_id_json_array = json_decode( $echoprint_server_output[ 0 ], TRUE );

	if( !$snippet_id_json_array[ 'track_id' ] )
	{



		// die( 'no results found' );
		die( '{ "error": "no results found" }' );



	}

// var_dump($snippet_id_json_array[ 'track_id' ]);
// var_dump($snippet_id_json_array);

	mysql_connect( 'localhost', 'root', 'password' ) or die( mysql_error() );

	$track_profile_resource = mysql_query( "SELECT artist, title FROM Songle.track WHERE track_id = \"{$snippet_id_json_array[ 'track_id' ]}\"" ) or die( mysql_error() );

	$track_profile_array = mysql_fetch_array( $track_profile_resource, MYSQL_ASSOC );

// var_dump($track_profile_array);

	echo json_encode( $track_profile_array, TRUE );

	// echo '{ state: "success" }';



?>
