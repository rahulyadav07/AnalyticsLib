# Image Downloading Library - System Design

## Overview
A clean, efficient, and easy-to-understand image downloading library for Android following SOLID principles and design patterns.

## Architecture Principles

### SOLID Principles Applied:
1. **Single Responsibility Principle (SRP)**: Each class has one reason to change
2. **Open/Closed Principle (OCP)**: Open for extension, closed for modification
3. **Liskov Substitution Principle (LSP)**: Derived classes must be substitutable for base classes
4. **Interface Segregation Principle (ISP)**: No client should depend on methods it doesn't use
5. **Dependency Inversion Principle (DIP)**: Depend on abstractions, not concretions

## Core Components

### 1. Core Interfaces (Contracts)
```
ImageLoader (Main Interface)
├── ImageCache (Caching Strategy)
├── NetworkClient (Network Operations)
├── ImageDecoder (Image Processing)
└── RequestManager (Request Lifecycle)
```

### 2. Design Patterns Used

#### Strategy Pattern
- **ImageCache**: Different caching strategies (Memory, Disk, NoCache)
- **NetworkClient**: Different network implementations (OkHttp, Volley, etc.)

#### Builder Pattern
- **ImageRequest**: Fluent API for building image requests
- **ImageLoaderConfig**: Configuration builder

#### Observer Pattern
- **ImageCallback**: Callback mechanism for async operations

#### Factory Pattern
- **ImageLoaderFactory**: Creates configured ImageLoader instances

#### Singleton Pattern
- **ImageLoader**: Single instance with global configuration

## Package Structure
```
com.rahulyadav.imagedownlinglib/
├── core/
│   ├── ImageLoader.kt (Main interface)
│   ├── ImageRequest.kt (Request model)
│   ├── ImageResult.kt (Result model)
│   └── ImageCallback.kt (Callback interface)
├── cache/
│   ├── ImageCache.kt (Cache interface)
│   ├── MemoryCache.kt (LruCache implementation)
│   ├── DiskCache.kt (File-based cache)
│   └── NoCache.kt (No caching)
├── network/
│   ├── NetworkClient.kt (Network interface)
│   └── OkHttpClient.kt (OkHttp implementation)
├── decoder/
│   ├── ImageDecoder.kt (Decoder interface)
│   └── BitmapDecoder.kt (Bitmap implementation)
├── config/
│   ├── ImageLoaderConfig.kt (Configuration)
│   └── CacheConfig.kt (Cache configuration)
└── factory/
    └── ImageLoaderFactory.kt (Factory for creating instances)
```

## Key Features

### 1. Simple API
```kotlin
// Basic usage
ImageLoader.load("https://example.com/image.jpg")
    .into(imageView)

// With callbacks
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView) { result ->
        when (result) {
            is ImageResult.Success -> // Handle success
            is ImageResult.Error -> // Handle error
        }
    }
```

### 2. Flexible Caching
- Memory cache with LRU eviction
- Disk cache with configurable size
- No cache option for real-time images

### 3. Error Handling
- Network errors
- Decoding errors
- Cache errors
- Timeout handling

### 4. Configuration
```kotlin
val config = ImageLoaderConfig.Builder()
    .memoryCacheSize(50 * 1024 * 1024) // 50MB
    .diskCacheSize(200 * 1024 * 1024)  // 200MB
    .timeout(30_000) // 30 seconds
    .build()

ImageLoader.initialize(context, config)
```

## Flow Diagram

```
User Request
    ↓
ImageLoader.load()
    ↓
Check Memory Cache
    ↓ (if miss)
Check Disk Cache
    ↓ (if miss)
Network Request
    ↓
Decode Image
    ↓
Store in Caches
    ↓
Return to UI
```

## Benefits of This Design

1. **Easy to Understand**: Clear separation of concerns
2. **Testable**: Each component can be unit tested independently
3. **Extensible**: Easy to add new caching strategies or network clients
4. **Maintainable**: Changes in one component don't affect others
5. **Performance**: Efficient caching and async operations
6. **Memory Safe**: Proper lifecycle management and memory cleanup

## Usage Examples

### Basic Usage
```kotlin
// Initialize once in Application class
ImageLoader.initialize(this)

// Use anywhere in your app
ImageLoader.load("https://example.com/image.jpg")
    .into(imageView)
```

### Advanced Usage
```kotlin
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .resize(200, 200)
    .centerCrop()
    .into(imageView) { result ->
        // Handle result
    }
```

This design makes the library easy to understand, maintain, and extend while following industry best practices.
