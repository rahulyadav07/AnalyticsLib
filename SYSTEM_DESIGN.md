# Analytics Library - System Design Documentation

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Initialization Flow](#initialization-flow)
4. [Component Roles](#component-roles)
5. [Data Flow](#data-flow)
6. [Communication Patterns](#communication-patterns)
7. [Sync Strategy](#sync-strategy)
8. [Error Handling](#error-handling)
9. [Performance Considerations](#performance-considerations)

## 🎯 Overview

The Analytics Library is a comprehensive Android analytics solution that provides reliable event tracking with automatic background syncing. It's designed to work seamlessly whether the app is running or not, ensuring no data loss.

### Key Features
- **Fully Automatic**: ContentProvider-based initialization with dual WorkManager setup
- **Hardcoded Configuration**: API key and endpoint built into the library
- **Dual Sync Strategy**: Immediate sync when app is running, WorkManager when not
- **Dual WorkManager**: Periodic + immediate sync for maximum reliability
- **Offline Support**: Local storage with Room database
- **Reliable Delivery**: Retry mechanisms and error handling
- **Zero Setup API**: Just log events - no initialization required

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Analytics Library                        │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐    ┌─────────────────┐                │
│  │   ContentProvider│    │   Static API    │                │
│  │ (Auto-Init)     │    │ (AnalyticImp)   │                │
│  └─────────────────┘    └─────────────────┘                │
│           │                       │                        │
│           ▼                       ▼                        │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                Core Components                          │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │ │
│  │  │QueueManager │ │StorageMgr   │ │NetworkMgr   │      │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘      │ │
│  └─────────────────────────────────────────────────────────┘ │
│           │                       │                        │
│           ▼                       ▼                        │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Background Components                      │ │
│  │  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐      │ │
│  │  │WorkManager  │ │SyncWorker   │ │Room Database│      │ │
│  │  └─────────────┘ └─────────────┘ └─────────────┘      │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 Initialization Flow

> **Note**: The diagrams below use Mermaid syntax. If they don't render properly in your viewer, refer to the text descriptions that follow each diagram.

### 1. ContentProvider Auto-Initialization
```mermaid
graph TD
    A["App Starts"] --> B["ContentProvider.onCreate()"]
    B --> C["AnalyticsInitProvider.onCreate()"]
    C --> D["Create AnalyticsConfig"]
    D --> E["AnalyticImp.init(context, config)"]
    E --> F["Initialize Components"]
    F --> G["Schedule Periodic WorkManager"]
    G --> H["Trigger Immediate WorkManager"]
    H --> I["Analytics Ready"]
    
    J["MainActivity.onCreate()"] --> K["Just Log Events"]
    K --> L["No Setup Required"]
```

### 2. Component Initialization Sequence
```
1. ContentProvider.onCreate()
   ├── Create AnalyticsConfig with default settings
   ├── Initialize AnalyticImp singleton
   └── Set up core components

2. Core Components Setup
   ├── QueueManager (event queuing)
   ├── StorageManager (Room database)
   ├── NetworkManager (API communication)
   └── WorkManager (background sync - auto-started)

3. Dual WorkManager Setup
   ├── Schedule periodic sync (every 15 minutes)
   ├── Trigger immediate sync on app start
   ├── Set network and battery constraints
   └── Ready for background processing

4. App Ready
   └── Just start logging events - no setup needed!
```

## 🧩 Component Roles

### 1. **AnalyticsInitProvider** (ContentProvider)
**Role**: Auto-initialization entry point
- **Responsibility**: Initialize analytics before Application.onCreate()
- **Lifecycle**: Created when app starts
- **Communication**: Sets up AnalyticImp singleton

### 2. **AnalyticImp** (Main Interface)
**Role**: Public API and orchestration
- **Responsibility**: 
  - Provide static methods for event logging
  - Coordinate between components
  - Manage singleton instance
- **Communication**: 
  - Receives events from app
  - Delegates to QueueManager
  - Manages WorkManager lifecycle

### 3. **QueueManager** (Event Management)
**Role**: Event queuing and batching
- **Responsibility**:
  - Queue incoming events
  - Batch events based on size/time
  - Trigger immediate sync when batch is full
- **Communication**:
  - Receives events from AnalyticImp
  - Stores events via StorageManager
  - Triggers NetworkManager for immediate sync

### 4. **StorageManager** (Data Persistence)
**Role**: Local data storage and retrieval
- **Responsibility**:
  - Store events in Room database
  - Retrieve pending events
  - Remove successfully sent events
- **Communication**:
  - Receives events from QueueManager
  - Provides events to SyncWorker
  - Manages database operations

### 5. **NetworkManager** (API Communication)
**Role**: Network communication
- **Responsibility**:
  - Send events to analytics API
  - Handle network errors
  - Return success/failure status
- **Communication**:
  - Receives events from QueueManager (immediate sync)
  - Receives events from SyncWorker (background sync)
  - Communicates with RetrofitAnalyticProvider

### 6. **AnalyticsWorkManager** (Background Sync)
**Role**: WorkManager coordination
- **Responsibility**:
  - Schedule periodic sync
  - Schedule immediate sync
  - Manage sync constraints
- **Communication**:
  - Receives sync requests from AnalyticImp
  - Coordinates with WorkManager system

### 7. **AnalyticsSyncWorker** (Background Worker)
**Role**: Background event syncing
- **Responsibility**:
  - Execute background sync
  - Handle retry logic
  - Clean up sent events
- **Communication**:
  - Receives work from WorkManager
  - Gets events from StorageManager
  - Sends events via NetworkManager

### 8. **Room Database** (Local Storage)
**Role**: Persistent storage
- **Responsibility**:
  - Store analytics events
  - Track sent/pending status
  - Provide efficient queries
- **Components**:
  - AnalyticsEventEntity
  - AnalyticsEventDao
  - AnalyticsDatabase

## 🔧 Hardcoded Configuration

The library uses hardcoded configuration for simplicity and security:

### **API Configuration**
```kotlin
// In RetrofitAnalyticProvider
private val endpoint = "https://api.analytics.rahulyadav.com/"
private val apiKey = "rahul_analytics_key_2024_secure_token"
```

### **Benefits of Hardcoded Configuration**
- **Security**: API key not exposed in app code
- **Simplicity**: No configuration required from developers
- **Consistency**: Same endpoint for all apps using the library
- **Maintenance**: Easy to update across all deployments

### **Request Headers**
All requests include these headers automatically:
```
Authorization: Bearer rahul_analytics_key_2024_secure_token
X-Analytics-SDK: RahulAnalytics
X-SDK-Version: 1.0.0
X-Device-Version: [Android Version]
X-Device-Model: [Device Model]
X-Device-Manufacturer: [Manufacturer]
X-Device-OS-Version: [SDK Version]
```

## 🔄 Data Flow

### Event Logging Flow
```mermaid
graph TD
    A["App calls AnalyticImp.logEvent()"] --> B["Create AnalyticsEvent"]
    B --> C["QueueManager.enqueue()"]
    C --> D["Store in Room Database"]
    D --> E{"Batch Size Reached?"}
    E -->|Yes| F["QueueManager.flush()"]
    E -->|No| G["Wait for more events"]
    F --> H["NetworkManager.sendEvents()"]
    H --> I{"Success?"}
    I -->|Yes| J["Remove from Database"]
    I -->|No| K["Keep in Database"]
    K --> L["WorkManager will retry"]
```

### Background Sync Flow
```mermaid
graph TD
    A["WorkManager Triggers"] --> B["AnalyticsSyncWorker.doWork()"]
    B --> C["StorageManager.getPendingEvents()"]
    C --> D["NetworkManager.sendEvents()"]
    D --> E{"Success?"}
    E -->|Yes| F["StorageManager.removeEvents()"]
    E -->|No| G["Result.retry()"]
    F --> H["Result.success()"]
```

## 📡 Communication Patterns

### 1. **Synchronous Communication**
- **AnalyticImp ↔ QueueManager**: Direct method calls
- **QueueManager ↔ StorageManager**: Coroutine-based calls
- **QueueManager ↔ NetworkManager**: Suspend function calls

### 2. **Asynchronous Communication**
- **WorkManager ↔ SyncWorker**: WorkManager framework
- **SyncWorker ↔ StorageManager**: Coroutine-based calls
- **SyncWorker ↔ NetworkManager**: Suspend function calls

### 3. **Data Flow Patterns**
```
Event Flow:
App → AnalyticImp → QueueManager → StorageManager → Room Database
                                    ↓
                              NetworkManager → API Server

Background Flow:
WorkManager → SyncWorker → StorageManager → NetworkManager → API Server
```

## ⚡ Sync Strategy

### Dual Sync Approach

#### 1. **Immediate Sync (App Running)**
- **Trigger**: When batch size is reached
- **Method**: Direct API call via NetworkManager
- **Advantages**: Real-time data, immediate feedback
- **Fallback**: If fails, events remain in database for background sync

#### 2. **Background Sync (App Not Running)**
- **Trigger**: Periodic WorkManager (every 15 minutes)
- **Method**: AnalyticsSyncWorker
- **Advantages**: Reliable delivery, battery efficient
- **Constraints**: Network available, battery not low

### Sync Decision Tree
```
Event Logged
├── Store in Database (Always)
├── Batch Size Reached?
│   ├── Yes → Try Immediate Sync
│   │   ├── Success → Remove from Database
│   │   └── Failure → Keep in Database
│   └── No → Wait for more events
└── Background Sync (Periodic)
    ├── Get Pending Events
    ├── Send via NetworkManager
    ├── Success → Remove from Database
    └── Failure → Retry with backoff
```

## 🛡️ Error Handling

### 1. **Network Errors**
- **Immediate Sync**: Events remain in database, WorkManager handles retry
- **Background Sync**: Exponential backoff retry
- **Max Retries**: Configurable (default: 3)

### 2. **Storage Errors**
- **Database Full**: Remove oldest events (FIFO)
- **Corruption**: Clear database and restart
- **Migration**: Room handles schema changes

### 3. **WorkManager Errors**
- **Constraints Not Met**: Retry when conditions are met
- **System Limits**: Respect Android's WorkManager limits
- **Battery Optimization**: Graceful degradation

## ⚡ Performance Considerations

### 1. **Memory Management**
- **Event Batching**: Reduces memory footprint
- **Database Cleanup**: Remove sent events promptly
- **Singleton Pattern**: Single instance reduces overhead

### 2. **Network Optimization**
- **Batch Requests**: Multiple events per API call
- **Header-Based Device Info**: Device info sent in headers, not with each event
- **Compression**: Gson serialization
- **Timeout Handling**: Configurable timeouts

### 3. **Battery Efficiency**
- **WorkManager Constraints**: Only sync when appropriate
- **Batch Processing**: Reduce CPU usage
- **Background Limits**: Respect Android's background restrictions

### 4. **Storage Efficiency**
- **Room Database**: Optimized queries
- **Indexing**: Efficient event retrieval
- **Cleanup**: Regular removal of sent events

## 🔧 Configuration

### Default Settings
```kotlin
AnalyticsConfig(
    batchSize = 10,                    // Events per batch
    batchTimeInterval = 5 * 60 * 1000, // 5 minutes
    maxQueueSize = 1000,               // Max events in queue
    maxRetryAttempts = 3,              // Retry attempts
    providers = listOf(...)            // Analytics providers
)
```

### WorkManager Settings
```kotlin
// Periodic sync every 15 minutes
schedulePeriodicSync(intervalMinutes = 15)

// Constraints
- Network: CONNECTED
- Battery: Not low
- Backoff: Exponential (10 seconds)
```

## 📊 Monitoring & Debugging

### 1. **Logging**
- **Event Logging**: Track event flow
- **Error Logging**: Network and storage errors
- **Performance Logging**: Sync timing and success rates

### 2. **Metrics**
- **Events Sent**: Success/failure counts
- **Sync Frequency**: Background sync timing
- **Storage Usage**: Database size and cleanup

### 3. **Debug Tools**
- **WorkManager Status**: Check sync worker status
- **Database Inspection**: View pending events
- **Network Logs**: API call success/failure

## 🚀 Usage Examples

### Basic Usage
```kotlin
// Initialize (automatic via ContentProvider)
AnalyticImp.init("your-api-key")

// Log events
AnalyticImp.logEvent("button_clicked")
AnalyticImp.logEvent("purchase", mapOf("amount" to 29.99))
```

### Advanced Usage
```kotlin
// Check WorkManager status
val workInfo = AnalyticsWorkManager(context).getWorkInfo()

// Manual sync trigger
AnalyticsWorkManager(context).scheduleImmediateSync()
```

This system design ensures reliable, efficient, and user-friendly analytics tracking with minimal impact on app performance and battery life.
