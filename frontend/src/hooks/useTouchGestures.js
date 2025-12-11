import { useCallback, useRef } from 'react';

/**
 * Custom hook for handling touch gestures in ReactFlow components
 * Provides pinch-to-zoom, pan, and double-tap functionality
 */
const useTouchGestures = () => {
  const lastTouchDistance = useRef(null);
  const lastTapTime = useRef(0);

  /**
   * Handles pinch-to-zoom gesture
   */
  const handlePinch = useCallback((event, reactFlowInstance) => {
    if (event.touches.length === 2) {
      const touch1 = event.touches[0];
      const touch2 = event.touches[1];
      
      const currentDistance = Math.hypot(
        touch1.clientX - touch2.clientX,
        touch1.clientY - touch2.clientY
      );

      if (lastTouchDistance.current !== null) {
        const zoom = reactFlowInstance.getZoom();
        const zoomDifference = currentDistance - lastTouchDistance.current;
        const newZoom = Math.max(0.5, Math.min(2, zoom + zoomDifference * 0.01));
        
        reactFlowInstance.setZoom(newZoom);
      }

      lastTouchDistance.current = currentDistance;
    }
  }, []);

  /**
   * Handles double-tap to reset zoom
   */
  const handleDoubleTap = useCallback((event, reactFlowInstance) => {
    const currentTime = new Date().getTime();
    const tapLength = currentTime - lastTapTime.current;
    
    if (tapLength < 300 && tapLength > 0) {
      // Double tap detected - reset zoom and position
      reactFlowInstance.setViewport({ x: 0, y: 0, zoom: 1 });
      event.preventDefault();
    }
    
    lastTapTime.current = currentTime;
  }, []);

  /**
   * Resets touch distance tracking
   */
  const resetTouch = useCallback(() => {
    lastTouchDistance.current = null;
  }, []);

  return {
    onTouchStart: (event, reactFlowInstance) => {
      if (event.touches.length === 2) {
        handlePinch(event, reactFlowInstance);
      } else if (event.touches.length === 1) {
        handleDoubleTap(event, reactFlowInstance);
      }
    },
    onTouchMove: (event, reactFlowInstance) => {
      if (event.touches.length === 2) {
        handlePinch(event, reactFlowInstance);
      }
    },
    onTouchEnd: resetTouch,
    onTouchCancel: resetTouch
  };
};

export default useTouchGestures;
