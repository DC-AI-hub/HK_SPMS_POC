import { useState, useCallback } from 'react';

/**
 * Generic modal management hook
 * @param {boolean} [initialState=false] - Initial open state
 * @returns {Object} Modal control functions and state
 */
const useModal = (initialState = false) => {
  const [isOpen, setIsOpen] = useState(initialState);

  const open = useCallback(() => setIsOpen(true), []);
  const close = useCallback(() => setIsOpen(false), []);
  const toggle = useCallback(() => setIsOpen(prev => !prev), []);

  return {
    isOpen,
    open,
    close,
    toggle
  };
};

export default useModal;
