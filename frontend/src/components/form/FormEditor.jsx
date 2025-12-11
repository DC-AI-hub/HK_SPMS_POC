import React, { useCallback, useEffect, useImperativeHandle, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';

import { FormEditor } from '@bpmn-io/form-js';

//import "@bpmn-io/form-js/"
import '@bpmn-io/form-js/dist/assets/form-js-base.css';
import '@bpmn-io/form-js/dist/assets/form-js-editor.css';
import '@bpmn-io/form-js/dist/assets/form-js.css';
import '@bpmn-io/form-js/dist/assets/properties-panel.css';
import '@bpmn-io/form-js/dist/assets/form-js-playground.css';
import '@bpmn-io/form-js/dist/assets/form-js-editor-base.css';


export default function BpmnFormEditor({
  initialSchema,
  onSchemaChange, config = {}, ref }) {

  const editorContainerRef = useRef(null);
  const editorRef = useRef(null);
  const lastSavedSchema = useRef(initialSchema);

  const saveSchema = useCallback(async () => {
    if (!editorRef.current) return;

    try {
      const schema = await editorRef.current.saveSchema();

      // 仅当有实际变化时触发回调
      if (JSON.stringify(schema) !== JSON.stringify(lastSavedSchema.current)) {
        lastSavedSchema.current = schema;
        onSchemaChange && onSchemaChange(schema);
      }
    } catch (err) {
      console.error('保存Schema失败:', err);
    }
  }, [onSchemaChange]);



  useEffect(() => {
    console.log("called -------")
    if (!editorContainerRef.current) return;

    // 创建编辑器实例
    editorRef.current = new FormEditor({
      container: editorContainerRef.current
    });
    editorRef.current.on('changed', 1, (e) => {
      debouncedSave(e.schema)
    });

    // 防抖处理变更事件
    const debouncedSave = debounce(saveSchema, 500);
    // 导入初始表单
    editorRef.current.importSchema(initialSchema).catch(err => {
      console.error('导入表单失败:', err);
    });

    // 清理函数
    return () => {
      if (editorRef.current) {
        editorRef.current.destroy();
      }
    };
  }, []);


  useEffect(()=>{
    if(editorRef.current){
      editorRef.current.importSchema(initialSchema).catch(err => {
      console.error('导入表单失败:', err);
    });
    }


  },[initialSchema])



  return (
    <div className="form-editor-container">
      <div
        ref={editorContainerRef}
        style={{ height: '80vh', border: '1px solid #ddd' }}
      ></div>
    </div>
  );
}


function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}