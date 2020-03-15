'use strict';

const SocksConnection = require('socksjs');
const mysql = require('mysql2');
const proxyUrl = process.env.IPB_SOCKS5;
const proxyValues = proxyUrl.split(new RegExp('[/(:\\/@)/]+'));

const mysqlServer = {
  host: 'your-host.example.com',
  port: 3306
};

const dbUser = 'user';
const dbPassword = 'password';
const db = 'database';

const proxyConnection = new SocksConnection(mysqlServer, {
  user: proxyValues[0],
  pass: proxyValues[1],
  host: proxyValues[2],
  port: proxyValues[3],
});

const mysqlConnPool = mysql.createPool({
  user: dbUser,
  password: dbPassword,
  database: db,
  stream: proxyConnection
});

mysqlConnPool.getConnection(function gotConnection(err, connection) {
  if (err) throw err;

  queryVersion(connection);
});

function queryVersion(connection) {
  connection.query('SELECT version();', function (err, rows, fields) {
      if (err) throw err;

      console.log('MySQL/MariaDB version: ', rows);
      connection.release();
      process.exit();
  });
}
