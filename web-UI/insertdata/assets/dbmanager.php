<?php
// -------------------------------------
// DBMANAGER.PHP
// permit to manage the connection
// with SQL database
// -------------------------------------

// -------------------------------------
// DB CREDENTIAL
// -------------------------------------
$db_username = "root";
$db_password = "root";
$db_name = "XDR_DB";
$db_host = "localhost";

// -------------------------------------
// CONNECTION INSTANCE
// -------------------------------------
$my_database = new db_manager();

// -------------------------------------
// DBMANAGER CLASS
// -------------------------------------
class db_manager {
    private $my_connection = null;
    
    function __construct() {
        $this->new_connection();
    }
    
    // create new connection and die in case of error
    function new_connection() {
        global $db_username;
        global $db_password;
        global $db_name;
        global $db_host;

        $this->my_connection = new mysqli($db_host, $db_username, $db_password, $db_name);
        if ($this->my_connection->connect_error) {
            echo("Can't connect to db, error " 
                . $this->my_connection->connect_errno . ": " 
                . $this->my_connection->connect_error);
            die(-1);
        }
    }
    
    // check if db is currently connected 
    // (if MYSQL:RECONNECT = TRUE in php.ini try to reconnect)
    function is_connected() {
        if ($this->my_connection == null)
            return false;
        else
            return $this->my_connection->ping();
    }

    // get last db error string
    function get_last_error() {
        return $this->my_connection->error;
    }

    // get last inserted id
    function get_last_id() {
        return $this->my_connection->insert_id;
    }

    // send query string to db
    function send_query($query_string) {
        if (!$this->is_connected()) {
            $this->new_connection();
        }

        return $this->my_connection->query($query_string);
    }
        
    // close database connection
    function close_connection() {
        $this->my_connection->close();
        $this->my_connection = null;
    }
}

?>
