# Analytics Library

A comprehensive Android analytics library with WorkManager integration for reliable background syncing of analytics events.

> 📖 **For detailed system design and architecture information, see [SYSTEM_DESIGN.md](SYSTEM_DESIGN.md)**

## Features

- **Event Logging**: Log custom events with parameters
- **Immediate Syncing**: Events sent immediately when app is running and batch is full
- **Background Syncing**: WorkManager integration for when app is not running
- **Local Storage**: Room database for offline event storage
- **Network Layer**: Retrofit-based API communication
- **Auto-Initialization**: Library initializes automatically via ContentProvider
- **Simple API**: Static methods - no need to call getInstance() every time
- **Configurable**: Flexible configuration for batch sizes, retry attempts, and more

## Setup

### 1. Add Dependencies

The library includes all necessary dependencies:
- WorkManager for background syncing
- Room for local storage
- Retrofit for network communication
- Gson for JSON serialization

### 2. Auto-Initialization

The library automatically initializes via ContentProvider when your app starts. Just set your API key:

```kotlin
// In your MainActivity or Application class
AnalyticImp.init("your-api-key")
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
// Old way (verbose)
AnalyticImp.getInstance().logEvent("button_clicked")

// New way (clean and simple)
AnalyticImp.logEvent("button_clicked")
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

1. Events are logged via `AnalyticImp.logEvent()`
2. Events are queued in `QueueManager` and stored locally
3. **When app is running**: Events sent immediately via API when batch size is reached
4. **When app is not running**: WorkManager periodically syncs events to the server
5. Successfully sent events are removed from local storage

### WorkManager Integration

The library uses WorkManager for reliable background syncing when the app is not running:

- **Periodic Sync**: Automatically syncs events every 15 minutes (configurable)
- **Network Constraints**: Only syncs when network is available
- **Retry Logic**: Exponential backoff for failed syncs
- **Battery Optimization**: Respects battery optimization settings
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

The library expects your API to accept events in the following format:

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
        "userId": null,
      "deviceInfo": {
        "version": "Android 13",
        "model": "Pixel 7",
        "manufacturer": "Google"
      }
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
