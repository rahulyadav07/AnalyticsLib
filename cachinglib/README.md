# Simple Cache Library

A clean, efficient, and super simple caching library for Android. Just get, put, remove, and clear - that's it!

## Features

- 🚀 **Super Simple API**: Just 4 methods - `get()`, `put()`, `remove()`, `clear()`
- 💾 **Smart Caching**: Automatic memory and disk caching with LRU eviction
- 🔄 **Async Operations**: Built with Kotlin Coroutines
- 🎯 **Clean Architecture**: 4-layer structure following SOLID principles
- 📱 **Memory Efficient**: Automatic cache size management
- 🧪 **Easy to Understand**: Perfect for interviews and learning

## Quick Start

### 1. Initialize the Library

```kotlin
// In your Application class
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize once - that's it!
        Cache.initialize(this)
    }
}
```

### 2. Basic Usage

```kotlin
// Store data
Cache.put("user_name", "John Doe")
Cache.put("user_age", 25)

// Retrieve data
val name: String? = Cache.get("user_name")
val age: Int? = Cache.get("user_age")

// Remove data
Cache.remove("user_name")

// Clear all caches
Cache.clear()
```

### 3. Object Caching

```kotlin
data class User(val id: Int, val name: String, val email: String)

val user = User(1, "Jane Doe", "jane@example.com")

// Store object
Cache.put("user_1", user)

// Retrieve object
val cachedUser: User? = Cache.get("user_1")
```

### 4. That's It!

The library is super simple - just 4 methods:
- `get(key)` - Get value from cache
- `put(key, value)` - Store value in cache
- `remove(key)` - Remove value from cache
- `clear()` - Clear all caches

## Package Structure

The library follows a clean 4-layer architecture:

```
📁 api/                    # Public API Layer
   ├── Cache.kt            # Main simple API
   └── ExampleUsage.kt     # Simple examples

📁 core/                   # Core Business Logic
   ├── CacheManager.kt     # Main implementation
   ├── CacheResult.kt      # Result model
   └── CacheException.kt   # Exception model

📁 strategy/               # Strategy Interfaces
   ├── Cache.kt            # Cache strategy
   └── Serializer.kt       # Serialization strategy

📁 implementation/         # Concrete Implementations
   ├── MemoryCache.kt      # Memory cache (LruCache)
   ├── DiskCache.kt        # Disk cache (FileSystem)
   ├── GsonSerializer.kt   # JSON serialization
   └── NoCache.kt          # Null object pattern

📁 config/                 # Configuration
   └── CacheConfig.kt      # Builder pattern config

📁 utils/                  # Utilities
   ├── Logger.kt
   └── KeyValidator.kt
```

## 🏗️ Architecture Overview

### Simple 4-Layer Design

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Simple Cache Library                                  │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────────┐    │
│  │                        Public API Layer                                │    │
│  │  ┌─────────────────────────────────────────────────────────────────┐   │    │
│  │  │                    Cache (Facade)                              │   │    │
│  │  │              get(), put(), remove(), clear()                   │   │    │
│  │  └─────────────────────────────────────────────────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────────────────────┘    │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                      Core Business Logic Layer                          │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐ │  │
│  │  │                    CacheManager                                   │ │  │
│  │  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐   │ │  │
│  │  │  │Memory       │  │Disk         │  │Serialization            │   │ │  │
│  │  │  │Orchestration│  │Orchestration│  │Management               │   │ │  │
│  │  │  └─────────────┘  └─────────────┘  └─────────────────────────┘   │ │  │
│  │  └─────────────────────────────────────────────────────────────────────┘ │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                        Strategy Layer (Interfaces)                      │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐         │  │
│  │  │Cache        │  │Serializer   │  │KeyValidator             │         │  │
│  │  │(Strategy)   │  │(Strategy)   │  │(Utility)                │         │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                    │                                            │
│  ┌─────────────────────────────────▼─────────────────────────────────────────┐  │
│  │                      Implementation Layer                                │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐         │  │
│  │  │MemoryCache  │  │DiskCache    │  │GsonSerializer           │         │  │
│  │  │(LruCache)   │  │(FileSystem) │  │(JSON)                   │         │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────────┘         │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Simple Flow

```
1. User calls Cache.put(key, value)
   ↓
2. CacheManager processes request
   ↓
3. Store in Memory Cache → Store in Disk Cache
   ↓
4. Return success result
```

### Cache Strategy

```
Memory Cache (Fast) → Disk Cache (Persistent)
       ↓                    ↓
   Return Value        Store for later
```

## Key Design Patterns

### 1. **Facade Pattern**
- `Cache` provides a simple interface to complex caching subsystems
- Hides the complexity of memory/disk caching and serialization

### 2. **Strategy Pattern**
- `Cache`, `Serializer` are strategies
- Easy to swap implementations (MemoryCache vs DiskCache vs NoCache)

### 3. **Builder Pattern**
- `CacheConfig.Builder` allows flexible configuration
- Easy to configure cache sizes and options

### 4. **Null Object Pattern**
- `NoCache` provides default behavior when caching is disabled
- Prevents null checks and simplifies code

### 5. **SOLID Principles**
- **Single Responsibility**: Each class has one job
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: All implementations are interchangeable
- **Interface Segregation**: Small, focused interfaces
- **Dependency Inversion**: Depends on abstractions, not concretions

## Why This Design?

### 🎯 **Perfect for Interviews**
- **Simple API**: Easy to explain and demonstrate
- **Clean Architecture**: Shows understanding of SOLID principles
- **Design Patterns**: Demonstrates knowledge of common patterns
- **Real-world Ready**: Production-quality code structure

### 🚀 **Key Benefits**
- **Easy to Understand**: Clear 4-layer structure
- **Easy to Extend**: Strategy pattern allows adding new implementations
- **Easy to Test**: Each component can be unit tested independently
- **Easy to Maintain**: Changes in one layer don't affect others

### 💡 **Interview Talking Points**
1. **SOLID Principles**: How each principle is applied
2. **Design Patterns**: Facade, Strategy, Builder, Null Object patterns
3. **Clean Architecture**: 4-layer separation of concerns
4. **Performance**: LRU eviction and cache size management
5. **Error Handling**: Comprehensive exception handling
6. **Threading**: Coroutines and proper dispatcher usage

This library is perfect for showcasing your Android development skills in interviews! 🎉
