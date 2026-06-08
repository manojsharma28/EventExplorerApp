import React from 'react';
import { View, Text, StyleSheet, FlatList, StatusBar, SafeAreaView } from 'react-native';

import EventItem from '../components/EventItem';

export default function HomeScreen({ events, onDelete }) {
  return (
    <SafeAreaView style={{ flex: 1 }}>
      <View style={styles.container}>
      <Text style={styles.title}>Events</Text>
      <FlatList
        data={events || []}   // always an array
        keyExtractor={(item, index) => item.id?.toString() || index.toString()}
        renderItem={({ item }) => <EventItem event={item} onDelete={onDelete} />}
        contentContainerStyle={styles.list}
      />
    </View>
    </SafeAreaView>
  );
}



const styles = StyleSheet.create({
  container: { flex: 1,  paddingTop: StatusBar.currentHeight, paddingHorizontal: 16, backgroundColor: '#fff' },
  title: { fontSize: 24, fontWeight: '600', marginBottom: 12 },
  list: { paddingBottom: 32 },
});
