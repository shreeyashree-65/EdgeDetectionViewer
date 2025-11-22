"use strict";
class EdgeDetectionViewer {
    constructor() {
        this.resolutionEl = document.getElementById('resolution');
        this.fpsEl = document.getElementById('fps');
        this.procTimeEl = document.getElementById('procTime');
        this.imageEl = document.getElementById('processedImage');
        this.stats = {
            resolution: '640x480',
            fps: 15,
            processingTime: 33
        };
        this.init();
    }
    init() {
        this.updateDisplay();
        this.setupEventListeners();
        this.loadSampleFrame();
    }
    setupEventListeners() {
        const loadBtn = document.getElementById('loadSampleBtn');
        const refreshBtn = document.getElementById('refreshBtn');
        loadBtn === null || loadBtn === void 0 ? void 0 : loadBtn.addEventListener('click', () => this.loadSampleFrame());
        refreshBtn === null || refreshBtn === void 0 ? void 0 : refreshBtn.addEventListener('click', () => this.refreshStats());
    }
    updateDisplay() {
        this.resolutionEl.textContent = this.stats.resolution;
        this.fpsEl.textContent = this.stats.fps.toString();
        this.procTimeEl.textContent = `${this.stats.processingTime}ms`;
    }
    loadSampleFrame() {
        // Generate a sample edge-detected pattern using canvas
        const canvas = document.createElement('canvas');
        canvas.width = 640;
        canvas.height = 480;
        const ctx = canvas.getContext('2d');
        // Create edge-like pattern
        ctx.fillStyle = '#000';
        ctx.fillRect(0, 0, 640, 480);
        ctx.strokeStyle = '#fff';
        ctx.lineWidth = 2;
        // Draw some edge patterns
        for (let i = 0; i < 50; i++) {
            ctx.beginPath();
            const x1 = Math.random() * 640;
            const y1 = Math.random() * 480;
            const x2 = x1 + (Math.random() * 100 - 50);
            const y2 = y1 + (Math.random() * 100 - 50);
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.stroke();
        }
        // Convert to image
        this.imageEl.src = canvas.toDataURL('image/png');
        console.log('Sample frame loaded');
    }
    refreshStats() {
        // Simulate stats update
        this.stats.fps = Math.floor(Math.random() * 10) + 10;
        this.stats.processingTime = Math.floor(Math.random() * 20) + 25;
        this.updateDisplay();
        console.log('Stats refreshed:', this.stats);
    }
    // Method to receive real frame data (for future WebSocket integration)
    updateFrame(base64Image, stats) {
        this.imageEl.src = `data:image/png;base64,${base64Image}`;
        if (stats.resolution)
            this.stats.resolution = stats.resolution;
        if (stats.fps)
            this.stats.fps = stats.fps;
        if (stats.processingTime)
            this.stats.processingTime = stats.processingTime;
        this.updateDisplay();
    }
}
// Initialize viewer when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const viewer = new EdgeDetectionViewer();
    // Expose to window for external access (e.g., WebSocket updates)
    window.edgeViewer = viewer;
    console.log('Edge Detection Viewer initialized');
});
