import { useEffect, useReducer } from "react";
import { Badge, Layout, Modal } from "antd";
import { LogoutOutlined } from "@ant-design/icons";
import axios from "axios";
import { TableContent } from "./TableContent";
import { FormContent } from "./FormContent";
import './App.css';
import { WebSocket } from "./WebSocket";
import { GenericButton, Delete } from "./buttons/buttons";
import { AuthenticationRetainer } from "./AuthenticationRetainer"
import { canEdit } from "./rest/crud";

const initialState = {
  viewState: {},
  detail: false,
  edit: false,
  id: null,
  configResp: null,
  configReq: null,
  alarms: new Map(),
  editEnable: false
}, reducer = (state, action) => {
  let alarms;
  switch (action.type) {
    case "table":
      return { ...state, viewState: action.payload, detail: false };
    case "detail":
      return { ...state, ...action.payload };
    case "config-resp":
      return { ...state, configResp: action.payload };
    case "config-req":
      return { ...state, configReq: action.payload };
    case "alarm":
      alarms = new Map(state.alarms);
      alarms.set(action.payload.id, action.payload);
      return { ...state, alarms }
    case "toggle-alarm":
      alarms = new Map(state.alarms);
      alarms.delete(action.payload);
      return { ...state, alarms }
    case "userEnable":
      return { ...state, editEnable: action.payload };
    default:
      return state;
  }
}, formatAlarm = obj => {
  const formatOpt = { year: "numeric", month: "numeric", day: "numeric", hour: "numeric", minute: "numeric", second: "numeric" };
  let msg = '';
  switch (obj.type) {
    case "connection":
      msg += `Connection lost at ${new Date(obj.time).toLocaleString("it-IT", formatOpt)}, last seen at ${new Date(obj.lastHB).toLocaleString("it-IT", formatOpt)}`;
      break;
    default:
      break;
  }
  console.log(msg);
  return msg;
};

function App() {
  const [{ viewState, detail, edit, id, configResp, configReq, alarms, editEnable }, dispatch] = useReducer(reducer, initialState);

  const { Header, Content, Footer } = Layout;

  useEffect(() => { canEdit().then(payload => dispatch({ type: "userEnable", payload })) }, [dispatch]);

  return (
    <Layout>
      <Header className="header">
        My IOT Device Handler
        <LogoutOutlined
          className="cp-logout"
          style={{ fontSize: 30, lineHeight: "64px" }}
          title={"Logout"}
          onClick={() => axios.post("/gateway/auth/logout").finally(() => window.location.reload())}
        />
      </Header>
      <Content className="content">
        {detail
          ?
          <FormContent {...{ edit, id, configResp, dispatch }} />
          :
          <TableContent {...{ viewState, editEnable, dispatch }} />
        }
      </Content>
      <Footer className="footer">
        <Badge count={alarms.size}>
          <GenericButton
            text="Alarms"
            disabled={alarms.size === 0}
            danger
            className="cp-show-alarm"
            onClick={() => Modal.info({
              title: "Active Alarms",
              content: <>
                {[...alarms.entries()].map((pair, key) => <p {...{ key }}>
                  <b>Agent {pair[0]}</b>: <span style={{ marginRight: 5 }}>{formatAlarm(pair[1])}</span>
                  <Delete danger={true} onClick={() => dispatch({ type: "toggle-alarm", payload: pair[0] })} />
                </p>)}
              </>,
              closable: true,
              centered: true,
              width: "60vw",
            })
            }
          />
        </Badge>
      </Footer>
      <WebSocket {...{ dispatch }} request={configReq} />
      <AuthenticationRetainer />
    </Layout>
  );
}

export default App;
