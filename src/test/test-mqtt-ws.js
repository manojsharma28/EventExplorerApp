// test-mqtt-ws.js
const mqtt = require('mqtt');
const c = mqtt.connect('ws://localhost:9001');
c.on('connect', ()=>{ c.subscribe('presence'); c.publish('presence','hello'); });
c.on('message',(t,m)=>{ console.log(t,m.toString()); c.end(); });