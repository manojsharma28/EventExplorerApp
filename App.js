import React, { useEffect, useState } from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import HomeScreen from './src/screens/HomeScreen';
import { Client as MQTTClient, Message } from 'paho-mqtt';

export default function App() {
  const [events, setEvents] = useState([]);

  useEffect(() => {
    if (typeof global.window === 'undefined') {
      global.window = global;
    }
    if (typeof global.WebSocket === 'undefined' && typeof WebSocket !== 'undefined') {
      global.WebSocket = WebSocket;
    }

    console.log('Connecting to MQTT via WebSocket...');

    const clientId = 'rn_' + Math.random().toString(16).substr(2, 8);
    const hostUri = 'ws://10.0.2.2:9001/';
    const client = new MQTTClient(hostUri, clientId);

    client.onConnectionLost = (responseObject) => {
      if (responseObject.errorCode !== 0) {
        console.error('MQTT connection lost', responseObject.errorMessage);
      } else {
        console.log('MQTT connection lost');
      }
    };

    client.onMessageArrived = (message) => {
      console.log('MQTT message', message.destinationName, message.payloadString);
      // Try to parse the payload as JSON and map to EventItem props
      let payload = message.payloadString;
      let parsed = null;
      try {
        parsed = JSON.parse(payload);
      } catch (e) {
        parsed = null;
      }

      const id = parsed && (parsed.id ?? parsed.Id) ? parsed.id ?? parsed.Id : Date.now();
      const rawSeverity = parsed && (parsed.severity ?? parsed.Severity);
      let severity = 'normal';
      if (rawSeverity) {
        const s = String(rawSeverity).toLowerCase();
        if (s === 'high' || s === 'critical') severity = 'critical';
        else if (s === 'medium' || s === 'warning') severity = 'warning';
      }

      const eventObj = {
        id,
        title: (parsed && (parsed.title ?? parsed.Title)) || message.destinationName,
        date: (parsed && (parsed.date ?? parsed.Date)) || new Date().toISOString().split('T')[0],
        source: (parsed && (parsed.source ?? parsed.Source)) || message.destinationName,
        description: (parsed && (parsed.description ?? parsed.Description)) || payload,
        severity,
        raw: parsed || payload,
      };

      setEvents((prev) => [...prev, eventObj]);
    };

    client.onMessageDelivered = (message) => {
      console.log('MQTT message delivered', message.destinationName);
    };

    client.connect({
      onSuccess: () => {
        console.log('MQTT connected', { connected: client.isConnected() });
        client.subscribe('Events', {
          qos: 0,
          onSuccess: (o) => console.log('Subscribed to Events', o),
          onFailure: (err) => console.error('Subscribe failed', err),
        });
      },
      onFailure: (err) => {
        console.error('MQTT connect failed', err);
      },
      useSSL: false,
      reconnect: true,
      cleanSession: true,
      mqttVersion: 4,
      timeout: 10,
    });

    return () => {
      try {
        client.disconnect();
      } catch (e) {
        console.error('Error disconnecting MQTT client', e);
      }
    };
  }, []);

  const handleDelete = (id) => {
    setEvents((prev) => prev.filter((e) => e.id !== id));
  };

  return (
    <SafeAreaView style={styles.container}>
      <HomeScreen events={events} onDelete={handleDelete} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
});
