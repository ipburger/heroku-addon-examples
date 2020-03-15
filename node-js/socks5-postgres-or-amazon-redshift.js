'use strict';

const pg = require('pg');
const SocksConnection = require('socksjs');

const proxyUrl = process.env.IPB_SOCKS5;
const proxyValues = proxyUrl.split(new RegExp('[/(:\\/@)/]+'));

const pgServer = {
  host: 'YOUR-HOST',
  port: 5432
};

const proxyConnection = new SocksConnection(pgServer, {
  user: proxyValues[0],
  pass: proxyValues[1],
  host: proxyValues[2],
  port: proxyValues[3],
});

const connectionConfig = {
  user: 'YOUR-DB-USERNAME',
  password: 'YOUR-DB-PASSWORD',
  database: 'YOUR-DATABASE',
  stream: proxyConnection,
  ssl: true // Optional, depending on db config
};

var client = new pg.Client(connectionConfig);

client.connect(function (err) {
  if (err) throw err;
  client.query('SELECT 1+1 as test1', function (err, result) {
    if (err) throw err;
    console.log(result.rows[0]);
    client.end(function (err) {
      if (err) throw err;
    });
  });
});
