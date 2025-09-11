# Analytics Library

A comprehensive Android analytics library with WorkManager integration for reliable background syncing of analytics events.

## 📚 Documentation

### Main Project Documentation
- **[SYSTEM_DESIGN.md](SYSTEM_DESIGN.md)** - Detailed system design, architecture, and component interactions
- **[PROJECT_OVERVIEW.md](PROJECT_OVERVIEW.md)** - Comprehensive project overview and quick start guide
- **[README.md](README.md)** - This file - Basic usage and setup

### Library-Specific Documentation
- **[Image Loading Library](imagedownlinglib/README.md)** - Clean, efficient image downloading library following SOLID principles
- **[Custom Broadcast System](custombtroadcast/README.md)** - Comprehensive custom broadcast receiver system with priority-based handling
- **[Caching Library](cachinglib/)** - Advanced caching mechanisms (documentation coming soon)
- **[Analytics Library](analytics/)** - Core analytics implementation (documentation in main README)

## 🏗️ Project Structure

This project contains multiple Android libraries, each designed with clean architecture and SOLID principles:

### 📊 Analytics Library (`analytics/`)
- **Purpose**: Event logging and analytics with WorkManager integration
- **Features**: Background syncing, local storage, batch processing
- **Key Components**: QueueManager, StorageManager, NetworkManager, WorkManager integration

### 🖼️ Image Loading Library (`imagedownlinglib/`)
- **Purpose**: Efficient image downloading and caching
- **Features**: Memory/disk caching, async loading, error handling
- **Key Components**: ImageLoader, ImageCache, NetworkClient, ImageDecoder
- **Design Patterns**: Strategy, Builder, Factory, Facade patterns

### 📡 Custom Broadcast System (`custombtroadcast/`)
- **Purpose**: Enhanced broadcast receiver system
- **Features**: Priority-based handling, lifecycle management, type-safe broadcasts
- **Key Components**: CustomBroadcastManager, BaseCustomBroadcastReceiver
- **Design Patterns**: Singleton, Observer, Strategy patterns

### 💾 Caching Library (`cachinglib/`)
- **Purpose**: Advanced caching mechanisms
- **Features**: Multi-level caching, cache strategies, performance optimization
- **Status**: In development

### 📱 Demo App (`app/`)
- **Purpose**: Demonstrates usage of all libraries
- **Features**: Integration examples, testing scenarios, UI demonstrations

## Features

- **Event Logging**: Log custom events with parameters
- **Immediate Syncing**: Events sent immediately when app is running and batch is full
- **Background Syncing**: WorkManager integration for when app is not running
- **Local Storage**: Room database for offline event storage
- **Network Layer**: Retrofit-based API communication
- **Auto-Initialization**: Library initializes automatically via ContentProvider
- **Simple API**: Static methods - no need to call getInstance() every time
- **Efficient Network**: Device info sent in headers, not with each event
- **Configurable**: Flexible configuration for batch sizes, retry attempts, and more

## Setup

### 1. Add Dependencies

The library includes all necessary dependencies:
- WorkManager for background syncing
- Room for local storage
- Retrofit for network communication
- Gson for JSON serialization

### 2. Fully Automatic Initialization

The library automatically initializes everything via ContentProvider when your app starts. No setup required!

```kotlin
// No initialization needed - everything is automatic!
// Just start logging events immediately
```

### 3. ContentProvider Registration

The analytics library uses a ContentProvider for initialization, which is automatically registered. No additional setup required!

The ContentProvider is already configured in the library and will initialize analytics before your Application's `onCreate()` is called.

#### Benefits of ContentProvider Initialization:
- **Early Initialization**: Initializes before Application.onCreate()
- **Reliable**: Always initializes regardless of app lifecycle
- **No Setup Required**: Automatically registered in the library
- **Performance**: No impact on app startup time

#### Simple API Usage:
```kotlin
// Just log events - no initialization needed!
AnalyticImp.logEvent("button_clicked", mapOf("source" to "main_screen"))
```

## Usage

### Logging Events

```kotlin
// Simple event
AnalyticImp.logEvent("button_clicked")

// Event with parameters
AnalyticImp.logEvent(
    "purchase_completed",
    mapOf(
        "product_id" to "123",
        "price" to 29.99,
        "currency" to "USD"
    )
)
```



## Architecture Overview

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

### Core Components

1. **AnalyticsInitProvider**: Auto-initialization via ContentProvider
2. **AnalyticImp**: Main analytics implementation with static API
3. **QueueManager**: Manages event queuing and batching
4. **StorageManager**: Handles local storage with Room database
5. **NetworkManager**: Manages API communication
6. **AnalyticsWorkManager**: WorkManager integration for background syncing
7. **AnalyticsSyncWorker**: Background worker for syncing events

### Data Flow

```
1. App Start
   └── ContentProvider.onCreate()
       └── AnalyticsInitProvider
           └── Auto-Initialize Analytics
               └── Analytics Ready

2. User calls AnalyticImp.logEvent()
   └── Event Created
       └── QueueManager.enqueue()
           └── StorageManager.persistEvents()
               └── Stored in Room Database

3. Batch Size Check
   ├── Batch Size Reached?
   │   ├── Yes → QueueManager.flush()
   │   │   └── NetworkManager.sendEvents()
   │   │       ├── Success → Remove from Database
   │   │       └── Failure → Keep in Database
   │   └── No → Wait for more events

4. Background Sync (Periodic)
   └── WorkManager Triggers
       └── AnalyticsSyncWorker.doWork()
           └── Retry Failed Events
```

**Key Points:**
1. **Auto-Initialization**: ContentProvider sets up everything automatically
2. **Dual Storage**: Events stored locally AND queued for processing
3. **Immediate Sync**: When app is running and batch is full
4. **Background Sync**: WorkManager handles failed/queued events
5. **No Data Loss**: Events always stored locally first
6. **Efficient Processing**: No redundant background processes or memory loading
7. **Smart Recovery**: Automatically syncs existing data when app starts

### Smart Recovery System

The library automatically handles existing data when the app starts:

**Scenario**: App was closed with pending events in database
**Solution**: Dual WorkManager approach

```kotlin
// When app starts, library automatically:
1. Schedules periodic WorkManager (every 15 minutes)
2. Triggers immediate WorkManager sync
3. Ensures data is sent as soon as possible
4. No waiting for periodic schedule
```

### WorkManager Integration

The library uses a dual WorkManager approach for maximum reliability:

- **Periodic Sync**: Automatically syncs events every 15 minutes (configurable)
- **Immediate Sync**: Triggers sync immediately when app starts
- **Network Constraints**: Only syncs when network is available
- **Retry Logic**: Exponential backoff for failed syncs
- **Battery Optimization**: Respects battery optimization settings

**Benefits of Dual Approach:**
- **No Waiting**: Immediate sync on app start
- **Backup Safety**: Periodic sync as fallback
- **Maximum Reliability**: Two sync mechanisms
- **Fallback Only**: WorkManager only handles events when app is not running

## Configuration Options

```kotlin
AnalyticsConfig(
    batchSize = 50,                    // Events per batch
    batchTimeInterval = 5 * 60 * 1000, // Batch interval in milliseconds
    maxQueueSize = 1000,               // Maximum events in queue
    maxRetryAttempts = 3,              // Retry attempts for failed requests
    providers = listOf(...)            // Analytics providers
)
```

## API Integration

The library uses hardcoded configuration for simplicity and security:

**Hardcoded Configuration:**
- **Endpoint**: `https://api.analytics.rahulyadav.com/`
- **API Key**: `rahul_analytics_key_2024_secure_token`

**Request Headers:**
```
Authorization: Bearer rahul_analytics_key_2024_secure_token
X-Analytics-SDK: RahulAnalytics
X-SDK-Version: 1.0.0
X-Device-Version: Android 13
X-Device-Model: Pixel 7
X-Device-Manufacturer: Google
X-Device-OS-Version: 33
```

**Request Body:**
```json
{
  "events": [
    {
      "id": "uuid",
      "name": "event_name",
      "params": {
        "key": "value"
      },
      "timestamp": 1234567890,
      "sessionId": null,
      "userId": null
    }
  ],
  "batchId": "batch_uuid",
  "timestamp": 1234567890,
  "sdkVersion": "1.0.0"
}
```

## Error Handling

- Network failures are handled gracefully with retry logic
- Events are stored locally when network is unavailable
- Failed events are retried with exponential backoff
- WorkManager ensures reliable delivery even after app restarts

## Performance Considerations

- Events are batched to reduce network calls
- Local storage prevents data loss
- Background syncing doesn't impact app performance
- Configurable batch sizes and intervals for optimization

## License

This project is licensed under the MIT License.
