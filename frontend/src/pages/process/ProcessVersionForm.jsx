import React, { useState, useRef } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Button, Box, CircularProgress, Tooltip, Typography
} from '@mui/material';
import DescriptionIcon from '@mui/icons-material/Description';
import AccountTreeIcon from '@mui/icons-material/AccountTree';
import ProcessDesigner from '../../components/bpmn/ProcessDesigner';
import ConfirmationDialog from '../../components/ConfirmationDialog';
import VersionInput from '../../components/common/VersionInput';
import UserSelector from '../../components/common/UserSelector';
import FormSelector from '../../components/process/FormSelector';
const initProcess = `
<?xml version="1.0" encoding="UTF-8"?>
<definitions
        xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
        xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL"
        xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
        xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"
        xmlns:di="http://www.omg.org/spec/DD/20100524/DI"
        xmlns:flowable="http://flowable.org/bpmn"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="
            http://www.omg.org/spec/BPMN/20100524/MODEL
            http://www.omg.org/spec/BPMN/20100524/BPMN20.xsd
            http://flowable.org/bpmn
            http://flowable.org/bpmn/flowable-bpmn-extensions.xsd"
        id="Definitions_1"
        targetNamespace="http://spms.com"
        exporterVersion="10.2.0">
    <!-- Process Definition -->
    <bpmn:process id="hr-leave-process" name="Leave Request Process" isExecutable="true">

        <!-- Start Event -->
        <bpmn:startEvent id="StartEvent_1">
            <bpmn:outgoing>SequenceFlow_1</bpmn:outgoing>
        </bpmn:startEvent>

        <!-- Employee Submission -->
        <bpmn:userTask id="submitRequest" name="Submit Leave Request"
                       flowable:assignee="\${employee}"
                       flowable:formKey="leaveRequestForm">
            <bpmn:extensionElements>
                <flowable:taskListener event="create" class="com.spms.backend.service.process.task.event.SpmsTaskCreateListener"/>
            </bpmn:extensionElements>
            <bpmn:incoming>SequenceFlow_1</bpmn:incoming>
            <bpmn:outgoing>SequenceFlow_2</bpmn:outgoing>
        </bpmn:userTask>

        <!-- Manager Approval -->
        <bpmn:userTask id="managerApproval" name="Approve Leave Request"
                       flowable:candidateGroups="managers"
                       flowable:formKey="approvalForm">
            <bpmn:incoming>SequenceFlow_2</bpmn:incoming>
            <bpmn:outgoing>SequenceFlow_3</bpmn:outgoing>
        </bpmn:userTask>

        <!-- Approval Decision -->
        <bpmn:exclusiveGateway id="decisionGateway" name="Approved?">
            <bpmn:incoming>SequenceFlow_3</bpmn:incoming>
            <bpmn:outgoing>SequenceFlow_4</bpmn:outgoing>
            <bpmn:outgoing>SequenceFlow_5</bpmn:outgoing>
        </bpmn:exclusiveGateway>

        <!-- Approval Path -->
        <bpmn:serviceTask id="notifyApproval" name="Send Approval Notification"
                          flowable:class="com.spms.backend.service.e2e.LeaveApprovalService">
            <bpmn:incoming>SequenceFlow_4</bpmn:incoming>
            <bpmn:outgoing>SequenceFlow_6</bpmn:outgoing>
        </bpmn:serviceTask>

        <!-- Rejection Path -->
        <bpmn:userTask id="handleRejection" name="Handle Rejection"
                       flowable:assignee="\${employee}">
            <bpmn:incoming>SequenceFlow_5</bpmn:incoming>
            <bpmn:outgoing>SequenceFlow_7</bpmn:outgoing>
        </bpmn:userTask>

        <!-- End Events -->
        <bpmn:endEvent id="endApproved" name="Leave Approved">
            <bpmn:incoming>SequenceFlow_6</bpmn:incoming>
        </bpmn:endEvent>

        <bpmn:endEvent id="endRejected" name="Leave Rejected">
            <bpmn:incoming>SequenceFlow_7</bpmn:incoming>
        </bpmn:endEvent>

        <!-- Sequence Flows -->
        <bpmn:sequenceFlow id="SequenceFlow_1" sourceRef="StartEvent_1" targetRef="submitRequest"/>
        <bpmn:sequenceFlow id="SequenceFlow_2" sourceRef="submitRequest" targetRef="managerApproval"/>
        <bpmn:sequenceFlow id="SequenceFlow_3" sourceRef="managerApproval" targetRef="decisionGateway"/>
        <bpmn:sequenceFlow id="SequenceFlow_4" sourceRef="decisionGateway" targetRef="notifyApproval">
            <bpmn:conditionExpression xsi:type="bpmn:tFormalExpression">
                \${approved}
            </bpmn:conditionExpression>
        </bpmn:sequenceFlow>
        <bpmn:sequenceFlow id="SequenceFlow_5" sourceRef="decisionGateway" targetRef="handleRejection">
            <bpmn:conditionExpression xsi:type="bpmn:tFormalExpression">
                \${!approved}
            </bpmn:conditionExpression>
        </bpmn:sequenceFlow>
        <bpmn:sequenceFlow id="SequenceFlow_6" sourceRef="notifyApproval" targetRef="endApproved"/>
        <bpmn:sequenceFlow id="SequenceFlow_7" sourceRef="handleRejection" targetRef="endRejected"/>
    </bpmn:process>

    <!-- DI Visualization -->
    <bpmndi:BPMNDiagram id="BPMNDiagram_1">
        <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="leaveRequest">
            <!-- Start Event -->
            <bpmndi:BPMNShape id="StartEvent_1_di" bpmnElement="StartEvent_1">
                <dc:Bounds x="152" y="102" width="36" height="36"/>
            </bpmndi:BPMNShape>

            <!-- Tasks -->
            <bpmndi:BPMNShape id="submitRequest_di" bpmnElement="submitRequest">
                <dc:Bounds x="240" y="80" width="100" height="80"/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape id="managerApproval_di" bpmnElement="managerApproval">
                <dc:Bounds x="400" y="80" width="100" height="80"/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape id="notifyApproval_di" bpmnElement="notifyApproval">
                <dc:Bounds x="620" y="60" width="100" height="80"/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape id="handleRejection_di" bpmnElement="handleRejection">
                <dc:Bounds x="620" y="180" width="100" height="80"/>
            </bpmndi:BPMNShape>

            <!-- Gateway -->
            <bpmndi:BPMNShape id="decisionGateway_di" bpmnElement="decisionGateway">
                <dc:Bounds x="550" y="95" width="50" height="50"/>
            </bpmndi:BPMNShape>

            <!-- End Events -->
            <bpmndi:BPMNShape id="endApproved_di" bpmnElement="endApproved">
                <dc:Bounds x="772" y="82" width="36" height="36"/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape id="endRejected_di" bpmnElement="endRejected">
                <dc:Bounds x="772" y="202" width="36" height="36"/>
            </bpmndi:BPMNShape>

            <!-- Sequence Flows -->
            <bpmndi:BPMNEdge id="SequenceFlow_1_di" bpmnElement="SequenceFlow_1">
                <di:waypoint x="188" y="120"/>
                <di:waypoint x="240" y="120"/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge id="SequenceFlow_2_di" bpmnElement="SequenceFlow_2">
                <di:waypoint x="340" y="120"/>
                <di:waypoint x="400" y="120"/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge id="SequenceFlow_3_di" bpmnElement="SequenceFlow_3">
                <di:waypoint x="500" y="120"/>
                <di:waypoint x="550" y="120"/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge id="SequenceFlow_4_di" bpmnElement="SequenceFlow_4">
                <di:waypoint x="575" y="95"/>
                <di:waypoint x="575" y="60"/>
                <di:waypoint x="620" y="100"/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge id="SequenceFlow_5_di" bpmnElement="SequenceFlow_5">
                <di:waypoint x="575" y="145"/>
                <di:waypoint x="575" y="180"/>
                <di:waypoint x="620" y="220"/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge id="SequenceFlow_6_di" bpmnElement="SequenceFlow_6">
                <di:waypoint x="720" y="100"/>
                <di:waypoint x="772" y="100"/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge id="SequenceFlow_7_di" bpmnElement="SequenceFlow_7">
                <di:waypoint x="720" y="220"/>
                <di:waypoint x="772" y="220"/>
            </bpmndi:BPMNEdge>
        </bpmndi:BPMNPlane>
    </bpmndi:BPMNDiagram>
</definitions>`
const ProcessVersionForm = ({ open, definitionId, onClose, onCreate, fullScreen = false, maxWidth = "xl" }) => {
  const { t } = useTranslation();
  const [name, setName] = useState('');
  const [key, setKey] = useState('');
  const [formKey, setFormKey] = useState('');
  const [formVersion, setFormVersion] = useState('');
  const [version, setVersion] = useState('');
  const [bpmnXml, setBpmnXml] = useState('');
  const [description, setDescription] = useState('');
  const [ownerId, setOwnerId] = useState(null);
  const [businessOwnerId, setBusinessOwnerId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [isDesignerFullScreen, setIsDesignerFullScreen] = useState(false);

  var tempXml = initProcess;

  const validateBpmnXml = (xml) => {
    try {
      // Basic validation - check for required elements
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xml, "text/xml");

      // Check for parsing errors
      const parserErrors = xmlDoc.getElementsByTagName("parsererror");
      if (parserErrors.length > 0) {
        return t('process:version.errors.invalidXml');
      }

      // Check for at least one process element
      const processes = xmlDoc.getElementsByTagName("bpmn:process");
      if (processes.length === 0) {
        return t('process:version.errors.missingProcess');
      }

      // Check for at least one start event
      const startEvents = xmlDoc.getElementsByTagName("bpmn:startEvent");
      if (startEvents.length === 0) {
        return t('process:version.errors.missingStartEvent');
      }

      return null;
    } catch (error) {
      return t('process:version.errors.invalidXml');
    }
  };

  const handleCreate = () => {
    const newErrors = {};
    if (!name) newErrors.name = t('process:version.errors.nameRequired');
    if (!key) newErrors.key = t('process:version.errors.keyRequired');
    if (!version) newErrors.version = t('process:version.errors.versionRequired');
    if (!bpmnXml) newErrors.designer = t('process:version.errors.designerRequired');

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    // Validate BPMN XML
    const bpmnError = validateBpmnXml(bpmnXml);
    if (bpmnError) {
      setErrors(prev => ({ ...prev, bpmnStructure: bpmnError }));
      return;
    }

    setShowConfirmation(true);
  };

  const handleCreateConfirmed = async () => {
    setLoading(true);
    try {
      await onCreate(definitionId, {
        name,
        key,
        version,
        formKey,
        formVersion,
        bpmnXml,
        description,
        ownerId,
        businessOwnerId
      });
      onClose();
    } finally {
      setLoading(false);
      setShowConfirmation(false);
    }
  };

  const handleDesignerChange = (xml) => {
    tempXml = xml;
    console.log("-----------", xml)
    if (errors.designer) setErrors(prev => ({ ...prev, designer: null }));
  };

  const handleVersionChange = (e) => {
    setVersion(e.target.value);
    if (errors.version) setErrors(prev => ({ ...prev, version: null }));
  };

  const renderFields = () => {

    return (<Box >
      <Box sx={{ mb: 2 }}>
        <TextField
          label={t('process:version.name')}
          value={name}
          onChange={(e) => setName(e.target.value)}
          error={!!errors.name}
          helperText={errors.name}
          fullWidth
          margin="normal"
          required
        />
      </Box>
      <Box sx={{ mb: 2 }}>
        <TextField
          label={t('process:version.key')}
          value={key}
          onChange={(e) => setKey(e.target.value)}
          error={!!errors.key}
          helperText={errors.key}
          fullWidth
          margin="normal"
          required
        />
      </Box>

      <Box sx={{ mb: 2 }}>
        <FormSelector 
          onKeySelected={setFormKey}
          onVersionSelected={setFormVersion}
        />
      </Box>
      <Box sx={{ mb: 2 }}>
        <VersionInput
          value={version}
          onChange={handleVersionChange}
          error={!!errors.version}
          helperText={errors.version}
          required
          processId={definitionId}
        />
      </Box>
      <Box sx={{ mb: 2 }}>
        <TextField
          label={t('process:version.description')}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          multiline
          rows={2}
          fullWidth
          margin="normal"
        />
      </Box>
    </Box>
    )
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth={maxWidth} fullWidth fullScreen={isDesignerFullScreen}>
      <DialogTitle>
        <Box display="flex" alignItems="center">
          <Typography variant="h6" component="span">
            {t('process:version.createTitle')}
          </Typography>
          <Box sx={{ flexGrow: 1 }} />
          <Tooltip title={formVersion ? t('process:version.formSelected') : t('process:version.formNotSelected')}>
            <DescriptionIcon 
              sx={{ ml: 2 }} 
              color={formVersion ? "success" : "disabled"} 
            />
          </Tooltip>
          <Tooltip title={bpmnXml ? t('process:version.processDefined') : t('process:version.processNotDefined')}>
            <AccountTreeIcon 
              sx={{ ml: 1 }} 
              color={bpmnXml ? "success" : "disabled"} 
            />
          </Tooltip>
        </Box>
      </DialogTitle>
      <DialogContent dividers>
        {!isDesignerFullScreen && renderFields()}

        {isDesignerFullScreen && <Box sx={{  border: errors.designer ? '1px solid red' : '1px solid #ccc',height:"100%" }}>
          <ProcessDesigner
            initialXml={bpmnXml}
            onChange={handleDesignerChange}
            onfullScreen={() => {
              setBpmnXml(tempXml)
              setIsDesignerFullScreen(!isDesignerFullScreen)
            }}
          />
        </Box>}
        
        {errors.designer && (
          <Box sx={{ color: 'error.main', mt: 1 }}>{errors.designer}</Box>
        )}
        {errors.bpmnStructure && (
          <Box sx={{ color: 'error.main', mt: 1 }}>{errors.bpmnStructure}</Box>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={()=> setIsDesignerFullScreen(true)} disabled={isDesignerFullScreen || loading} >
          {t('process:open-designer')}
        </Button>
        <Button onClick={onClose} disabled={loading}>
          {t('common:cancel')}
        </Button>
        <Button
          variant="contained"
          onClick={handleCreate}
          disabled={loading}>
          {loading ? <CircularProgress size={24} /> : t('common:save')}
        </Button>
      </DialogActions>

      <ConfirmationDialog
        open={showConfirmation}
        onClose={() => setShowConfirmation(false)}
        onConfirm={handleCreateConfirmed}
        title={t('confirmation.title')}
        message={t('confirmation.createVersion')}
      />
    </Dialog>
  );
};

export default ProcessVersionForm;
