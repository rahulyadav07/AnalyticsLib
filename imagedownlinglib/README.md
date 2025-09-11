# Image Downloading Library

A clean, efficient, and easy-to-understand image downloading library for Android following SOLID principles and design patterns.

## Features

- 🚀 **Simple API**: Easy-to-use fluent interface
- 💾 **Smart Caching**: Memory and disk caching with LRU eviction
- 🔄 **Async Operations**: Built with Kotlin Coroutines
- 🎯 **SOLID Principles**: Clean architecture and maintainable code
- 🛡️ **Error Handling**: Comprehensive error handling and recovery
- 📱 **Memory Efficient**: Automatic image resizing and memory management
- 🔧 **Configurable**: Flexible configuration options
- 🧪 **Testable**: Well-structured for unit testing

## Quick Start

### 1. Initialize the Library

```kotlin
// In your Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize with default configuration
        ImageLoader.initialize(this)
        
        // Or with custom configuration
        val config = ImageLoaderConfig.Builder(this)
            .memoryCacheSize(100 * 1024 * 1024) // 100MB
            .diskCacheSize(500 * 1024 * 1024)   // 500MB
            .timeout(60) // 60 seconds
            .enableLogging()
            .build()
        
        ImageLoader.initialize(this, config)
    }
}
```

### 2. Basic Usage

```kotlin
// Simple image loading
ImageLoader.load("https://example.com/image.jpg")
    .into(imageView)

// With placeholder and error handling
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView)

// With resizing and scaling
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .resize(200, 200)
    .centerCrop()
    .into(imageView)
```

### 3. Advanced Usage with Callbacks

```kotlin
ImageLoader.load("https://example.com/image.jpg")
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .into(imageView) { result ->
        when (result) {
            is ImageResult.Success -> {
                // Image loaded successfully
                println("Image loaded: ${result.bitmap.width}x${result.bitmap.height}")
            }
            is ImageResult.Error -> {
                // Handle error
                println("Error: ${result.exception.message}")
            }
            is ImageResult.Loading -> {
                // Handle loading state
                println("Loading...")
            }
        }
    }
```

## 🏗️ High-Level Design (HLD)

### System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Image Loading Library                                 │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                        Public API Layer                                 │    │
│  │  ┌─────────────────┐  ┌───────────────────┐  ┌─────────────────────────┐│    │
│  │  │   ImageLoader   │  │ImageRequestBuilder│  │   ImageLoaderFactory    ││    │
│  │  │   (Facade)      │  │   (Builder)       │  │     (Factory)           ││    │
│  │  └─────────────────┘  └───────────────────┘  └─────────────────────────┘│    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                      Core Business Logic Layer                            │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐  │  │
│  │  │                    ImageLoaderImpl                                  │  │  │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐ │  │  │
│  │  │  │Request      │  │Cache        │  │Network      │  │Image        │ │  │  │
│  │  │  │Management   │  │Orchestration│  │Orchestration│  │Processing   │ │  │  │
│  │  │  │             │  │             │  │             │  │             │ │  │  │
│  │  │  │- Job Queue  │  │- Memory     │  │- Download   │  │- Decode     │ │  │  │
│  │  │  │- Lifecycle  │  │- Disk       │  │- Retry      │  │- Resize     │ │  │  │
│  │  │  │- Cancellation│  │- Strategy   │  │- Timeout    │  │- Transform │ │  │  │
│  │  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘ │  │  │
│  │  └─────────────────────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                        Strategy Layer (Interfaces)                        │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐   │  │
│  │  │ImageCache   │  │NetworkClient│  │ImageDecoder │  │ImageCallback    │   │  │
│  │  │(Strategy)   │  │(Strategy)   │  │(Strategy)   │  │(Observer)       │   │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────┘   │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                      Implementation Layer                                 │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐   │  │
│  │  │MemoryCache  │  │DiskCache    │  │OkHttpClient │  │BitmapDecoder    │   │  │
│  │  │(LruCache)   │  │(FileSystem) │  │(HTTP)       │  │(BitmapFactory)  │   │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────┘   │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐   │  │
│  │  │NoCache      │  │CustomCache  │  │CustomClient │  │CustomDecoder    │   │  │
│  │  │(Null Object)│  │(Extensible) │  │(Extensible) │  │(Extensible)     │   │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────┘   │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Component Communication Flow

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Image Loading Flow                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  1. User Request                                                                │
│     ┌─────────────┐                                                             │
│     │ ImageLoader │                                                             │
│     │ .load(url)  │                                                             │
│     └─────┬───────┘                                                             │
│           │                                                                     │
│  2. Request Processing                                                          │
│           ▼                                                                     │
│     ┌─────────────────────────────────────────────────────────────────────────┐ │
│     │                    ImageLoaderImpl                                     │ │
│     │  ┌─────────────────────────────────────────────────────────────────┐   │ │
│     │  │                Request Lifecycle Management                    │   │ │
│     │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │   │ │
│     │  │  │Cancel       │  │Job Queue    │  │UI Thread                │ │   │ │
│     │  │  │Existing     │  │Management   │  │Dispatch                 │ │   │ │
│     │  │  │Request      │  │             │  │                         │ │   │ │
│     │  │  └─────────────┘  └─────────────┘  └─────────────────────────┘ │   │ │
│     │  └─────────────────────────────────────────────────────────────────┘   │ │
│     └─────────────────────────────────────────────────────────────────────────┘ │
│           │                                                                     │
│  3. Cache Strategy Execution                                                   │
│           ▼                                                                     │
│     ┌─────────────────────────────────────────────────────────────────────────┐ │
│     │                        Cache Layer                                     │ │
│     │  ┌─────────────────────────────────────────────────────────────────┐   │ │
│     │  │                    Cache Strategy                               │   │ │
│     │  │                                                                 │   │ │
│     │  │  1. Memory Cache Check                                          │   │ │
│     │  │     ┌─────────────┐                                             │   │ │
│     │  │     │MemoryCache  │ ──► Hit? ──► Return Bitmap                  │   │ │
│     │  │     │(LruCache)   │     │                                       │   │ │
│     │  │     └─────────────┘     │ No                                    │   │ │
│     │  │                         ▼                                       │   │ │
│     │  │  2. Disk Cache Check                                            │   │ │
│     │  │     ┌─────────────┐                                             │   │ │
│     │  │     │DiskCache    │ ──► Hit? ──► Store in Memory + Return       │   │ │
│     │  │     │(FileSystem) │     │                                       │   │ │
│     │  │     └─────────────┘     │ No                                    │   │ │
│     │  │                         ▼                                       │   │ │
│     │  │  3. Network Download                                            │   │ │
│     │  │     ┌─────────────┐                                             │   │ │
│     │  │     │NetworkClient│ ──► Download ──► Store in Both Caches       │   │ │
│     │  │     │(OkHttp)     │                                             │   │ │
│     │  │     └─────────────┘                                             │   │ │
│     │  └─────────────────────────────────────────────────────────────────┘   │ │
│     └─────────────────────────────────────────────────────────────────────────┘ │
│           │                                                                     │
│  4. Image Processing                                                            │
│           ▼                                                                     │
│     ┌─────────────────────────────────────────────────────────────────────────┐ │
│     │                    Image Processing Layer                              │ │
│     │  ┌─────────────────────────────────────────────────────────────────┐   │ │
│     │  │                    ImageDecoder                                 │   │ │
│     │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │   │ │
│     │  │  │Decode       │  │Resize       │  │Transform                │ │   │ │
│     │  │  │ByteArray    │  │Image        │  │(CenterCrop,             │ │   │ │
│     │  │  │to Bitmap    │  │Dimensions   │  │ CenterInside)           │ │   │ │
│     │  │  └─────────────┘  └─────────────┘  └─────────────────────────┘ │   │ │
│     │  └─────────────────────────────────────────────────────────────────┘   │ │
│     └─────────────────────────────────────────────────────────────────────────┘ │
│           │                                                                     │
│  5. Result Delivery                                                             │
│           ▼                                                                     │
│     ┌─────────────────────────────────────────────────────────────────────────┐ │
│     │                        Result Layer                                    │ │
│     │  ┌─────────────────────────────────────────────────────────────────┐   │ │
│     │  │                    Result Processing                            │   │ │
│     │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐ │   │ │
│     │  │  │Success      │  │Error        │  │Loading                  │ │   │ │
│     │  │  │Result       │  │Result       │  │State                    │ │   │ │
│     │  │  │             │  │             │  │                         │ │   │ │
│     │  │  │- Set Image  │  │- Set Error  │  │- Set Placeholder        │ │   │ │
│     │  │  │- Callback   │  │- Callback   │  │- Progress Indicator     │ │   │ │
│     │  │  └─────────────┘  └─────────────┘  └─────────────────────────┘ │   │ │
│     │  └─────────────────────────────────────────────────────────────────┘   │ │
│     └─────────────────────────────────────────────────────────────────────────┘ │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Component Interaction Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        Component Interaction Flow                              │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │   Client    │    │ImageLoader  │    │ImageLoader  │    │   ImageView     │  │
│  │   Code      │    │  (Facade)   │    │   Impl      │    │                 │  │
│  └─────┬───────┘    └─────┬───────┘    └─────┬───────┘    └─────────────────┘  │
│        │                  │                  │                                  │
│        │ 1. load(url)     │                  │                                  │
│        ├─────────────────►│                  │                                  │
│        │                  │ 2. create request│                                  │
│        │                  ├─────────────────►│                                  │
│        │                  │                  │                                  │
│        │                  │                  │ 3. check memory cache           │
│        │                  │                  ├─────────────────────────────────┤
│        │                  │                  │                                  │
│        │                  │                  │ 4. check disk cache             │
│        │                  │                  ├─────────────────────────────────┤
│        │                  │                  │                                  │
│        │                  │                  │ 5. download from network        │
│        │                  │                  ├─────────────────────────────────┤
│        │                  │                  │                                  │
│        │                  │                  │ 6. decode image                 │
│        │                  │                  ├─────────────────────────────────┤
│        │                  │                  │                                  │
│        │                  │ 7. result        │                                  │
│        │                  │◄─────────────────┤                                  │
│        │ 8. update UI     │                  │                                  │
│        │◄─────────────────┤                  │                                  │
│        │                  │                  │                                  │
│        │                  │                  │ 9. store in caches              │
│        │                  │                  ├─────────────────────────────────┤
│        │                  │                  │                                  │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐  │
│  │   Client    │    │ImageLoader  │    │ImageLoader  │    │   ImageView     │  │
│  │   Code      │    │  (Facade)   │    │   Impl      │    │                 │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────────┘  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Data Flow Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Data Flow Architecture                               │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                        Request Flow                                    │    │
│  │                                                                         │    │
│  │  URL ──► ImageRequest ──► ImageLoaderImpl ──► Cache Strategy ──► Result │    │
│  │                                                                         │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                        Cache Flow                                       │  │
│  │                                                                         │  │
│  │  Memory Cache ──► Disk Cache ──► Network Download ──► Image Processing  │  │
│  │       │                │                │                    │          │  │
│  │       ▼                ▼                ▼                    ▼          │  │
│  │  [LruCache]      [FileSystem]      [OkHttp]            [BitmapFactory]  │  │
│  │                                                                         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                        Result Flow                                      │  │
│  │                                                                         │  │
│  │  Success ──► Error ──► Loading ──► UI Update ──► Callback               │  │
│  │     │          │          │           │            │                    │  │
│  │     ▼          ▼          ▼           ▼            ▼                    │  │
│  │  [Bitmap]  [Exception] [State]   [ImageView]   [ImageCallback]          │  │
│  │                                                                         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Component Communication Details

#### 1. **Public API Layer Components**

| Component | Responsibility | Communication |
|-----------|---------------|---------------|
| **ImageLoader** | Main facade interface | Receives user requests, delegates to ImageLoaderImpl |
| **ImageRequestBuilder** | Fluent API builder | Builds ImageRequest objects, chains configuration |
| **ImageLoaderFactory** | Singleton factory | Creates and manages ImageLoader instances |

#### 2. **Core Business Logic Components**

| Component | Responsibility | Communication |
|-----------|---------------|---------------|
| **ImageLoaderImpl** | Main orchestrator | Coordinates all operations, manages request lifecycle |
| **Request Management** | Job queue and lifecycle | Tracks active requests, handles cancellation |
| **Cache Orchestration** | Cache strategy execution | Manages cache hierarchy (Memory → Disk → Network) |
| **Network Orchestration** | Network operations | Handles downloads, retries, timeouts |
| **Image Processing** | Image transformation | Decodes, resizes, transforms images |

#### 3. **Strategy Layer Components**

| Component | Responsibility | Communication |
|-----------|---------------|---------------|
| **ImageCache** | Caching strategy interface | Defines cache operations contract |
| **NetworkClient** | Network operations interface | Defines network operations contract |
| **ImageDecoder** | Image processing interface | Defines image decoding contract |
| **ImageCallback** | Result delivery interface | Defines callback mechanism contract |

#### 4. **Implementation Layer Components**

| Component | Responsibility | Communication |
|-----------|---------------|---------------|
| **MemoryCache** | LruCache implementation | Stores bitmaps in memory with LRU eviction |
| **DiskCache** | File system cache | Stores images on disk with size management |
| **NoCache** | Null object pattern | Provides no-op cache implementation |
| **OkHttpNetworkClient** | HTTP client implementation | Downloads images using OkHttp |
| **BitmapDecoder** | Image decoder implementation | Decodes images using BitmapFactory |

### Communication Flow Sequence

```
1. Client Code
   ↓ (calls ImageLoader.load())
   
2. ImageLoader (Facade)
   ↓ (delegates to ImageLoaderImpl)
   
3. ImageLoaderImpl
   ↓ (creates ImageRequest)
   ↓ (checks active requests)
   ↓ (starts coroutine)
   
4. Cache Strategy Execution
   ↓ (MemoryCache.get())
   ↓ (if miss: DiskCache.get())
   ↓ (if miss: NetworkClient.download())
   
5. Image Processing
   ↓ (ImageDecoder.decode())
   ↓ (resize/transform if needed)
   
6. Cache Storage
   ↓ (MemoryCache.put())
   ↓ (DiskCache.put())
   
7. Result Delivery
   ↓ (ImageCallback.onResult())
   ↓ (ImageView.setImageBitmap())
```

### Threading Model

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Threading Architecture                               │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                        Main Thread (UI)                               │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐       │    │
│  │  │ImageLoader  │  │ImageView    │  │ImageCallback            │       │    │
│  │  │API Calls    │  │Updates      │  │onResult()               │       │    │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘       │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                    Background Threads (IO)                              │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐         │  │
│  │  │MemoryCache  │  │DiskCache    │  │NetworkClient            │         │  │
│  │  │Operations   │  │Operations   │  │Download Operations      │         │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘         │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐         │  │
│  │  │ImageDecoder │  │File I/O     │  │HTTP Requests            │         │  │
│  │  │Operations   │  │Operations   │  │Response Processing      │         │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                    Coroutine Dispatchers                                │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐         │  │
│  │  │Dispatchers  │  │Dispatchers  │  │Dispatchers              │         │  │
│  │  │.Main        │  │.IO          │  │.Default                 │         │  │
│  │  │(UI Updates) │  │(I/O Ops)    │  │(CPU Intensive)          │         │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Error Handling Flow

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Error Handling Architecture                          │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                        Error Types                                     │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐       │    │
│  │  │Network      │  │Decode       │  │Cache                    │       │    │
│  │  │Exception    │  │Exception    │  │Exception                │       │    │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘       │    │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐       │    │
│  │  │Timeout      │  │OutOfMemory  │  │InvalidUrl               │       │    │
│  │  │Exception    │  │Exception    │  │Exception                │       │    │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘       │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                        Error Handling Flow                              │  │
│  │                                                                         │  │
│  │  1. Exception Occurs                                                   │  │
│  │     ↓                                                                   │  │
│  │  2. Catch in ImageLoaderImpl                                           │  │
│  │     ↓                                                                   │  │
│  │  3. Create ImageResult.Error                                           │  │
│  │     ↓                                                                   │  │
│  │  4. Dispatch to Main Thread                                            │  │
│  │     ↓                                                                   │  │
│  │  5. Update UI (Error Image)                                            │  │
│  │     ↓                                                                   │  │
│  │  6. Call ImageCallback.onResult()                                      │  │
│  │                                                                         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Architecture

The library follows SOLID principles and uses several design patterns:

### SOLID Principles

1. **Single Responsibility Principle (SRP)**: Each class has one reason to change
2. **Open/Closed Principle (OCP)**: Open for extension, closed for modification
3. **Liskov Substitution Principle (LSP)**: Derived classes are substitutable for base classes
4. **Interface Segregation Principle (ISP)**: No client depends on methods it doesn't use
5. **Dependency Inversion Principle (DIP)**: Depend on abstractions, not concretions

### Design Patterns

- **Strategy Pattern**: Different caching and network implementations
- **Builder Pattern**: Fluent API for configuration and requests
- **Factory Pattern**: Creating configured instances
- **Facade Pattern**: Simple interface to complex subsystems
- **Observer Pattern**: Callback mechanism for async operations

### Package Structure

```
com.rahulyadav.imagedownlinglib/
├── core/                    # Core interfaces and models
├── cache/                   # Caching implementations
├── network/                 # Network layer
├── decoder/                 # Image decoding
├── config/                  # Configuration classes
├── factory/                 # Factory classes
├── utils/                   # Utility classes
└── ImageLoader.kt          # Main public API
```

## Configuration Options

### Memory Cache
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .memoryCacheSize(50 * 1024 * 1024) // 50MB
    .disableMemoryCache() // Disable memory cache
    .build()
```

### Disk Cache
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .diskCacheSize(200 * 1024 * 1024) // 200MB
    .disableDiskCache() // Disable disk cache
    .build()
```

### Network Configuration
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .timeout(30) // 30 seconds timeout
    .networkClient(customNetworkClient) // Custom network client
    .build()
```

### Logging
```kotlin
val config = ImageLoaderConfig.Builder(context)
    .enableLogging() // Enable debug logging
    .build()
```

## Cache Management

```kotlin
// Get cache statistics
val stats = ImageLoader.getCacheStats()
println("Memory: ${stats.memoryCacheSize}/${stats.memoryCacheMaxSize}")
println("Disk: ${stats.diskCacheSize}/${stats.diskCacheMaxSize}")

// Clear all caches
ImageLoader.clearCache()
```

## Request Management

```kotlin
// Cancel specific request
ImageLoader.cancel("https://example.com/image.jpg")

// Cancel all pending requests
ImageLoader.cancelAll()
```

## Error Handling

The library provides comprehensive error handling with specific exception types:

```kotlin
ImageLoader.load("https://example.com/image.jpg")
    .into(imageView) { result ->
        when (result) {
            is ImageResult.Error -> {
                when (result.exception) {
                    is ImageLoaderException.NetworkException -> {
                        // Handle network errors
                    }
                    is ImageLoaderException.DecodeException -> {
                        // Handle decoding errors
                    }
                    is ImageLoaderException.CacheException -> {
                        // Handle cache errors
                    }
                    is ImageLoaderException.OutOfMemoryException -> {
                        // Handle memory issues
                    }
                }
            }
        }
    }
```

## Performance Tips

1. **Use appropriate cache sizes** based on your app's memory constraints
2. **Resize images** to match your UI requirements
3. **Use centerCrop()** for consistent aspect ratios
4. **Cancel requests** when views are no longer visible
5. **Enable logging** only in debug builds

## Testing

The library is designed to be easily testable:

```kotlin
// Mock dependencies for testing
val mockCache = mockk<ImageCache>()
val mockNetworkClient = mockk<NetworkClient>()
val mockDecoder = mockk<ImageDecoder>()

val config = ImageLoaderConfig.Builder(context)
    .memoryCache(mockCache)
    .networkClient(mockNetworkClient)
    .imageDecoder(mockDecoder)
    .build()
```

## Dependencies

- **Kotlin Coroutines**: For async operations
- **OkHttp**: For network requests
- **AndroidX Core**: For context and lifecycle management

## Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Kotlin**: 1.8+
- **Java**: 11+

## License

This library is part of the AnalyticsLib project and follows the same licensing terms.

## Contributing

When contributing to this library:

1. Follow SOLID principles
2. Write unit tests for new features
3. Update documentation
4. Ensure backward compatibility
5. Follow the existing code style

## Interview Tips

When explaining this library in interviews, focus on:

1. **SOLID Principles**: How each principle is applied
2. **Design Patterns**: Which patterns are used and why
3. **Architecture**: Clean separation of concerns
4. **Performance**: Caching strategies and memory management
5. **Error Handling**: Comprehensive error scenarios
6. **Testability**: How the design enables easy testing
7. **Extensibility**: How to add new features without breaking existing code

This library demonstrates professional Android development practices and is perfect for showcasing your understanding of clean architecture and design patterns.
