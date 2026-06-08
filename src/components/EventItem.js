import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';

export default function EventItem({ event, onDelete }) {
  return (
    <View style={[styles.container, styles[event.severity || 'normal']] }>
      <View style={styles.header}>
        <Text style={styles.title}>{event.title}</Text>
        <TouchableOpacity onPress={() => onDelete(event.id)} style={styles.deleteButton}>
          <Text style={styles.deleteText}>Delete</Text>
        </TouchableOpacity>
      </View>
      <Text style={styles.meta}>{event.date} • {event.source}</Text>
      <Text style={styles.desc}>{event.description}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 12,
    borderRadius: 8,
    marginBottom: 10,
  },
  title: { fontSize: 16, fontWeight: '600', marginBottom: 4 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  meta: { fontSize: 12, color: '#444', marginBottom: 6 },
  desc: { fontSize: 14, color: '#222' },
  deleteButton: { backgroundColor: '#ffe5e5', borderRadius: 4, paddingVertical: 4, paddingHorizontal: 10 },
  deleteText: { color: '#c00', fontSize: 12, fontWeight: '700' },
  normal: { backgroundColor: '#f1f5f9' },
  warning: { backgroundColor: '#fff4e5' },
  critical: { backgroundColor: '#ffe5e5' },
});
