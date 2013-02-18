package com.chessyoup.ui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chessyoup.R;
import com.chessyoup.game.GameManager;
import com.chessyoup.game.view.ChessBoardPlay;
import com.chessyoup.game.view.ColorTheme;
import com.chessyoup.ui.adapters.MainViewPagerAdapter;
import com.chessyoup.ui.fragments.FragmenChat;
import com.chessyoup.ui.fragments.FragmentGame;
import com.cyp.chess.chessboard.ChessboardController;
import com.cyp.chess.chessboard.ChessboardMode;
import com.cyp.chess.chessboard.ChessboardStatus;
import com.cyp.chess.chessboard.ChessboardUIInterface;
import com.cyp.chess.game.ChessGame;
import com.cyp.chess.game.ChessGameListener;
import com.cyp.chess.model.Game.GameState;
import com.cyp.chess.model.GameTree.Node;
import com.cyp.chess.model.Move;
import com.cyp.chess.model.Position;
import com.cyp.chess.model.TextIO;
import com.cyp.chess.model.pgn.PGNOptions;
import com.cyp.chess.model.pgn.PgnToken;
import com.cyp.chess.model.pgn.PgnTokenReceiver;
import com.cyp.game.IGameCommand;
import com.cyp.transport.Util;

public class ChessGameActivity extends FragmentActivity implements
		ChessboardUIInterface, ChessGameListener {

	private boolean boardGestures = true;

	private ChessBoardPlay cb;

	private ChessboardController ctrl;

	private PgnScreenText gameTextListener;

	private FragmentGame fGame;

	private FragmenChat fChat;

	private ViewPager gameViewPager;

	private DateFormat dateFormat;

	private ChessGame game;

	public ImageButton abortButton;

	public ImageButton resignButton;

	public ImageButton drawButton;

	public ImageButton exitButton;

	public ImageButton rematchButton;

	private boolean drawRequested;

	private boolean abortRequested;

	public void onCreate(Bundle savedInstanceState) {
		Log.d("ChessboardActivity", "on create");
		super.onCreate(savedInstanceState);
		dateFormat = new SimpleDateFormat("EEEE, kk:mm", Locale.getDefault());

		PGNOptions pgnOptions = new PGNOptions();
		this.gameTextListener = new PgnScreenText(pgnOptions);
		this.ctrl = new ChessboardController(this, this.gameTextListener,
				pgnOptions);

		game = GameManager.getManager().findGame(
				getIntent().getExtras().getString("remoteId"),
				getIntent().getExtras().getLong("gameId"));

		if (game != null) {
			this.initUI();
			this.installListeners();
			this.runSendReadyTask();
		} else {
			Log.d("ChessboardActivity", "No game found!");
			finish();
		}
	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chessboard);
		ColorTheme.instance().readColors(
				PreferenceManager.getDefaultSharedPreferences(this));

		cb = (ChessBoardPlay) findViewById(R.id.chessboard);
		cb.setFocusable(true);
		cb.requestFocus();
		cb.setClickable(true);
		cb.setPgnOptions(this.ctrl.getPgnOptions());

		this.abortButton = (ImageButton) findViewById(R.id.abortGameButton);
		this.resignButton = (ImageButton) findViewById(R.id.resignGameButton);
		this.drawButton = (ImageButton) findViewById(R.id.drawGameButton);
		this.exitButton = (ImageButton) findViewById(R.id.exitGameButton);
		this.rematchButton = (ImageButton) findViewById(R.id.rematchGameButton);

		this.gameViewPager = (ViewPager) this
				.findViewById(R.id.chessBoardViewPager);
		this.fChat = new FragmenChat();
		this.fGame = new FragmentGame();
		MainViewPagerAdapter fAdapter = new MainViewPagerAdapter(getSupportFragmentManager());
		fAdapter.addFragment(this.fGame);
		fAdapter.addFragment(this.fChat);
		this.gameViewPager.setAdapter(fAdapter);
		this.gameViewPager.setCurrentItem(1);
		this.gameViewPager.setCurrentItem(0);
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.border);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
		bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT,
				Shader.TileMode.REPEAT);
		this.findViewById(R.id.chessboardLayout).setBackgroundDrawable(
				bitmapDrawable);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("ChessboardActivity", "on start");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("ChessboardActivity", "on spause");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("ChessboardActivity", "on resume");
		Toast.makeText(this, "Whiting for oponent!", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("ChessboardActivity", "on stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("ChessboardActivity", "on destroy");
	}

	@Override
	public void setPosition(Position pos, String variantInfo,
			ArrayList<Move> variantMoves) {
		cb.setPosition(pos);
	}

	@Override
	public void setSelection(int sq) {
		cb.setSelection(sq);
		cb.userSelectedSquare = false;
	}

	@Override
	public void setStatus(ChessboardStatus s) {
		String str;
		switch (s.state) {
		case ALIVE:
			str = Integer.valueOf(s.moveNr).toString();
			if (s.white)
				str += ". " + getString(R.string.whites_move);
			else
				str += "... " + getString(R.string.blacks_move);
			if (s.ponder)
				str += " (" + getString(R.string.ponder) + ")";
			if (s.thinking)
				str += " (" + getString(R.string.thinking) + ")";
			if (s.analyzing)
				str += " (" + getString(R.string.analyzing) + ")";
			break;
		case WHITE_MATE:
			str = getString(R.string.white_mate);
			break;
		case BLACK_MATE:
			str = getString(R.string.black_mate);
			break;
		case WHITE_STALEMATE:
		case BLACK_STALEMATE:
			str = getString(R.string.stalemate);
			break;
		case DRAW_REP: {
			str = getString(R.string.draw_rep);
			if (s.drawInfo.length() > 0)
				str = str + " [" + s.drawInfo + "]";
			break;
		}
		case DRAW_50: {
			str = getString(R.string.draw_50);
			if (s.drawInfo.length() > 0)
				str = str + " [" + s.drawInfo + "]";
			break;
		}
		case DRAW_NO_MATE:
			str = getString(R.string.draw_no_mate);
			break;
		case DRAW_AGREE:
			str = getString(R.string.draw_agree);
			break;
		case RESIGN_WHITE:
			str = getString(R.string.resign_white);
			break;
		case RESIGN_BLACK:
			str = getString(R.string.resign_black);
			break;
		default:
			str = "unknown";
		}		
	}
	
	@Override
	public void requestPromotePiece() {
		promoteDialog().show();
	}

	@Override
	public void runOnUIThread(Runnable runnable) {
		this.runOnUiThread(runnable);
	}

	@Override
	public void reportInvalidMove(Move m) {
		String msg = String.format("%s %s-%s",
				getString(R.string.invalid_move),
				TextIO.squareToString(m.from), TextIO.squareToString(m.to));
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setRemainingTime(long wTime, long bTime, long nextUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public String whitePlayerName() {
		return this.game.getWhitePlayer();
	}

	@Override
	public String blackPlayerName() {
		return this.game.getBlackPlayer();
	}

	@Override
	public boolean discardVariations() {
		return false;
	}

	@Override
	public void moveListUpdated() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				fGame.moveListView.setText(gameTextListener.getSpannableData());
				Layout layout = fGame.moveListView.getLayout();
				if (layout != null) {
					int currPos = gameTextListener.getCurrPos();
					int line = layout.getLineForOffset(currPos);
					int y = (int) ((line - 1.5) * fGame.moveListView
							.getLineHeight());
					fGame.moveListScroll.scrollTo(0, y);
				}
			}
		});
	}

	@Override
	public void setAnimMove(Position sourcePos, Move move, boolean forward) {
		cb.setAnimMove(sourcePos, move, forward);
	}

	private void installListeners() {
		this.game.addGameListener(this);

		final GestureDetector gd = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					private float scrollX = 0;
					private float scrollY = 0;

					@Override
					public boolean onDown(MotionEvent e) {
						if (!boardGestures) {
							handleClick(e);
							return true;
						}
						scrollX = 0;
						scrollY = 0;
						return false;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {

						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (!boardGestures)
							return false;
						cb.cancelLongPress();
						handleClick(e);
						return true;
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						if (!boardGestures)
							return false;
						if (e.getAction() == MotionEvent.ACTION_UP)
							handleClick(e);
						return true;
					}

					private final void handleClick(MotionEvent e) {
						if (true) {
							int sq = cb.eventToSquare(e);
							Move m = cb.mousePressed(sq);
							if (m != null) {
								if (ctrl.localTurn()) {
									ctrl.makeLocalMove(m);
									sendMoveToRemote(TextIO.moveToUCIString(m));
								}
							}
						}
					}
				});
		cb.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gd.onTouchEvent(event);
			}
		});

		fChat.runInstallListener = new Runnable() {

			@Override
			public void run() {
				fChat.chatSendMessageButton
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Log.d("GCMChatActivity",
										"Send message request.");
								runSendMessageTask(fChat.chatEditText
										.getEditableText().toString());
								fChat.chatEditText.setText("");
							}
						});

				fChat.chatEditText.setOnKeyListener(new View.OnKeyListener() {

					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						Log.d("key event", event.toString());

						if (event != null
								&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							runSendMessageTask(fChat.chatEditText
									.getEditableText().toString());
							fChat.chatEditText.setText("");
							InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							in.hideSoftInputFromWindow(v.getWindowToken(), 0);

							return true;
						}
						else{
							return false;
						}
					}
				});
			}
		};

		fGame.runInstallListeners = new Runnable() {

			@Override
			public void run() {
				abortButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (ctrl.getGame().getGameState() == GameState.ALIVE) {
							if (abortRequested) {
								ctrl.abortGame();
								fGame.moveListView.append(" aborted");
								abortRequested = false;
								try {
									game.acceptAbortRequest();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							} else {
								try {
									game.sendAbortRequest();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				});

				resignButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (ctrl.getGame().getGameState() == GameState.ALIVE) {
							ctrl.resignGame();

							try {
								game.resign();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});

				drawButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (ctrl.getGame().getGameState() == GameState.ALIVE) {
							if (drawRequested) {
								ctrl.drawGame();
								drawRequested = false;
								try {
									game.acceptDrawRequest();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							} else {
								try {
									ctrl.offerDraw();
									game.sendDrawRequest();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}
				});

				rematchButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (ctrl.getGame().getGameState() != GameState.ALIVE) {
							try {
								game.sendRematchRequest();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});

				exitButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (ctrl.getGame().getGameState() == GameState.ALIVE) {

							AlertDialog.Builder db = new AlertDialog.Builder(
									ChessGameActivity.this);
							db.setTitle("Resign?");
							String actions[] = new String[2];
							actions[0] = "Ok";
							actions[1] = "Cancel";
							db.setItems(actions,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											switch (which) {
											case 0:
												try {
													game.resign();
													game.sendGameClosed();
												} catch (IOException e) {
													e.printStackTrace();
												}

												ctrl.resignGame();
												game.getAccount()
														.getGameController()
														.closeGame(game);
												finish();
												break;
											case 1:
												break;
											default:

												break;
											}
										}
									});

							AlertDialog ad = db.create();
							ad.setCancelable(true);
							ad.setCanceledOnTouchOutside(false);
							ad.show();
						} else {
							try {
								game.sendGameClosed();
								ctrl.abortGame();
								game.getAccount().getGameController()
										.closeGame(game);
								finish();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});
			}
		};
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (this.ctrl.getGame().getGameState() == GameState.ALIVE) {
				AlertDialog.Builder db = new AlertDialog.Builder(this);
				db.setTitle("Warning");
				String actions[] = new String[2];
				actions[0] = "Resign!";
				actions[1] = "Cancel";
				db.setItems(actions, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							runResignTask();
							finish();
							break;
						case 1:
							break;
						default:

							break;
						}
					}
				});

				AlertDialog ad = db.create();
				ad.setCancelable(true);
				ad.setCanceledOnTouchOutside(false);
				ad.show();

				return true;
			} else {
				finish();
				return false;
			}
		} else {
			finish();
			return false;
		}
	}

	private void runResignTask() {
		this.ctrl.resignGame();

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {
					ctrl.resignGame();
					game.resign();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};

		task.execute();
	}

	private void runSendMessageTask(final String message) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				addMessage(Util.getContactFromId(game.getAccount().getConnection().getAccountId()), message);

				try {
					game.sendChat(message);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		task.execute();
	}

	private void runSendReadyTask() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(500);
					game.sendReady();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		task.execute();
	}

	private void sendMoveToRemote(final String move) {

		Log.d("ChessBoardActivity", "Send move to remote :" + move);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {
					game.sendMove(move);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		task.execute();
	}

	/**
	 * PngTokenReceiver implementation that renders PGN data for screen display.
	 */
	static class PgnScreenText implements PgnTokenReceiver {
		private SpannableStringBuilder sb = new SpannableStringBuilder();
		private int prevType = PgnToken.EOF;
		int nestLevel = 0;
		boolean col0 = true;
		Node currNode = null;
		final static int indentStep = 15;
		int currPos = 0, endPos = 0;
		boolean upToDate = false;
		PGNOptions options;

		private static class NodeInfo {
			int l0, l1;

			NodeInfo(int ls, int le) {
				l0 = ls;
				l1 = le;
			}
		}

		HashMap<Node, NodeInfo> nodeToCharPos;

		PgnScreenText(PGNOptions options) {
			nodeToCharPos = new HashMap<Node, NodeInfo>();
			this.options = options;
		}

		public final SpannableStringBuilder getSpannableData() {
			return sb;
		}

		public final int getCurrPos() {
			return currPos;
		}

		public boolean isUpToDate() {
			return upToDate;
		}

		int paraStart = 0;
		int paraIndent = 0;
		boolean paraBold = false;

		private final void newLine() {
			newLine(false);
		}

		private final void newLine(boolean eof) {
			if (!col0) {
				if (paraIndent > 0) {
					int paraEnd = sb.length();
					int indent = paraIndent * indentStep;
					sb.setSpan(new LeadingMarginSpan.Standard(indent),
							paraStart, paraEnd,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (paraBold) {
					int paraEnd = sb.length();
					sb.setSpan(new StyleSpan(Typeface.BOLD), paraStart,
							paraEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (!eof)
					sb.append('\n');
				paraStart = sb.length();
				paraIndent = nestLevel;
				paraBold = false;
			}
			col0 = true;
		}

		boolean pendingNewLine = false;

		/** Makes moves in the move list clickable. */
		private final static class MoveLink extends ClickableSpan {
			private Node node;

			MoveLink(Node n) {
				node = n;
			}

			@Override
			public void onClick(View widget) {
				// if (ctrl != null)
				// ctrl.goNode(node);
			}

			@Override
			public void updateDrawState(TextPaint ds) {
			}
		}

		public void processToken(Node node, int type, String token) {
			if ((prevType == PgnToken.RIGHT_BRACKET)
					&& (type != PgnToken.LEFT_BRACKET)) {
				if (options.view.headers) {
					col0 = false;
					newLine();
				} else {
					sb.clear();
					paraBold = false;
				}
			}
			if (pendingNewLine) {
				if (type != PgnToken.RIGHT_PAREN) {
					newLine();
					pendingNewLine = false;
				}
			}
			switch (type) {
			case PgnToken.STRING:
				sb.append(" \"");
				sb.append(token);
				sb.append('"');
				break;
			case PgnToken.INTEGER:
				if ((prevType != PgnToken.LEFT_PAREN)
						&& (prevType != PgnToken.RIGHT_BRACKET) && !col0)
					sb.append(' ');
				sb.append(token);
				col0 = false;
				break;
			case PgnToken.PERIOD:
				sb.append('.');
				col0 = false;
				break;
			case PgnToken.ASTERISK:
				sb.append(" *");
				col0 = false;
				break;
			case PgnToken.LEFT_BRACKET:
				sb.append('[');
				col0 = false;
				break;
			case PgnToken.RIGHT_BRACKET:
				sb.append("]\n");
				col0 = false;
				break;
			case PgnToken.LEFT_PAREN:
				nestLevel++;
				if (col0)
					paraIndent++;
				newLine();
				sb.append('(');
				col0 = false;
				break;
			case PgnToken.RIGHT_PAREN:
				sb.append(')');
				nestLevel--;
				pendingNewLine = true;
				break;
			case PgnToken.NAG:
				sb.append(Node.nagStr(Integer.parseInt(token)));
				col0 = false;
				break;
			case PgnToken.SYMBOL: {
				if ((prevType != PgnToken.RIGHT_BRACKET)
						&& (prevType != PgnToken.LEFT_BRACKET) && !col0)
					sb.append(' ');
				int l0 = sb.length();
				sb.append(token);
				int l1 = sb.length();
				nodeToCharPos.put(node, new NodeInfo(l0, l1));
				sb.setSpan(new MoveLink(node), l0, l1,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				if (endPos < l0)
					endPos = l0;
				col0 = false;
				if (nestLevel == 0)
					paraBold = true;
				break;
			}
			case PgnToken.COMMENT:
				if (prevType == PgnToken.RIGHT_BRACKET) {
				} else if (nestLevel == 0) {
					nestLevel++;
					newLine();
					nestLevel--;
				} else {
					if ((prevType != PgnToken.LEFT_PAREN) && !col0) {
						sb.append(' ');
					}
				}
				int l0 = sb.length();
				sb.append(token.replaceAll("[ \t\r\n]+", " ").trim());
				int l1 = sb.length();
				int color = ColorTheme.instance().getColor(
						ColorTheme.PGN_COMMENT);
				sb.setSpan(new ForegroundColorSpan(color), l0, l1,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				col0 = false;
				if (nestLevel == 0)
					newLine();
				break;
			case PgnToken.EOF:
				newLine(true);
				upToDate = true;
				break;
			}
			prevType = type;
		}

		@Override
		public void clear() {
			sb.clear();
			prevType = PgnToken.EOF;
			nestLevel = 0;
			col0 = true;
			currNode = null;
			currPos = 0;
			endPos = 0;
			nodeToCharPos.clear();
			paraStart = 0;
			paraIndent = 0;
			paraBold = false;
			pendingNewLine = false;

			upToDate = false;
		}

		BackgroundColorSpan bgSpan = new BackgroundColorSpan(0xff888888);

		@Override
		public void setCurrent(Node node) {
			sb.removeSpan(bgSpan);
			NodeInfo ni = nodeToCharPos.get(node);
			if ((ni == null) && (node != null) && (node.getParent() != null))
				ni = nodeToCharPos.get(node.getParent());
			if (ni != null) {
				int color = ColorTheme.instance().getColor(
						ColorTheme.CURRENT_MOVE);
				bgSpan = new BackgroundColorSpan(color);
				sb.setSpan(bgSpan, ni.l0, ni.l1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				currPos = ni.l0;
			} else {
				currPos = 0;
			}
			currNode = node;
		}
	}

	private void addMessage(final String source, final String text) {
		this.runOnUIThread(new Runnable() {

			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				sb.append("<span style=\"color:red\">")
						.append(dateFormat.format(new Date()))
						.append(" </span>");
				sb.append(source != null ? source : "null").append(" : ");
				sb.append(text != null ? text : "null").append("<br/>");
				Spanned styledText = Html.fromHtml(sb.toString());
				fChat.chatDisplay.append(styledText);
			}
		});
	}

	@Override
	public void localMoveMade(final Move move) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {
					game.sendMove(move);
				} catch (IOException e) {
					e.printStackTrace();
				}

				return null;
			}
		};

		task.execute();
	}

	@Override
	public void commandReceived(IGameCommand arg0) {
	}

	@Override
	public void abortAcceptedReceived() {

		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				ctrl.abortGame();
				fGame.moveListView.append(" abort accepted");
				Toast.makeText(ChessGameActivity.this, "Abort accepted!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void abortRequestReceived() {
		this.abortRequested = true;

		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				fGame.moveListView.append(" abort requested!");
				Toast.makeText(ChessGameActivity.this, "Abort requested!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void chatReceived(final String text) {
		this.runOnUIThread(new Runnable() {

			@Override
			public void run() {
				addMessage( Util.getContactFromId(game.getChallenge().getRemoteContact().getId()) , text);
				gameViewPager.setCurrentItem(1);
			}
		});
	}

	@Override
	public void drawAcceptedReceived() {

		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				ctrl.drawGame();
				gameViewPager.setCurrentItem(0);
				Toast.makeText(ChessGameActivity.this, "Draw accepted!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void drawRequestReceived() {
		this.drawRequested = true;

		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				ctrl.offerDraw();
				gameViewPager.setCurrentItem(0);
				Toast.makeText(ChessGameActivity.this, "Draw requested!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void gameClosedReceived() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				fGame.moveListView.append("Game closed!");
				gameViewPager.setCurrentItem(0);
				Toast.makeText(ChessGameActivity.this,
						"Opponent closed the board!", Toast.LENGTH_SHORT)
						.show();
			}

		});
	}

	@Override
	public void moveReceived(final String move) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				ctrl.makeRemoteMove(move);
				gameViewPager.setCurrentItem(0);
			}
		});
	}

	@Override
	public void readyReceived() {
		Log.d("ChessboardActivity", "readyReceived");

		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				ctrl.newGame(new ChessboardMode(game.getChallenge()
						.isReceived() ? ChessboardMode.TWO_PLAYERS_WHITE_REMOTE
						: ChessboardMode.TWO_PLAYERS_BLACK_REMOTE));
				cb.setFlipped(game.getChallenge().isReceived());
				ctrl.startGame();

				if (game.getChallenge().isReceived()) {

					if (game.getChallenge().getRemoteContact().getAvatar() != null) {
						Bitmap bmp = BitmapFactory.decodeByteArray(game
								.getChallenge().getRemoteContact().getAvatar(),
								0, game.getChallenge().getRemoteContact()
										.getAvatar().length);
						fGame.blackPlayerImageView.setImageBitmap(bmp);
					} else {
						fGame.blackPlayerImageView
								.setImageDrawable(getResources().getDrawable(
										R.drawable.general_avatar_unknown));
					}

					if (game.getAccount().getConnection().getAvatar() != null) {
						Bitmap bmp = BitmapFactory
								.decodeByteArray(game.getAccount()
										.getConnection().getAvatar(), 0, game
										.getAccount().getConnection()
										.getAvatar().length);
						fGame.whitePlayerImageView.setImageBitmap(bmp);
					} else {
						fGame.whitePlayerImageView
								.setImageDrawable(getResources().getDrawable(
										R.drawable.general_avatar_unknown));
					}
				} else {
					if (game.getChallenge().getRemoteContact().getAvatar() != null) {
						Bitmap bmp = BitmapFactory.decodeByteArray(game
								.getChallenge().getRemoteContact().getAvatar(),
								0, game.getChallenge().getRemoteContact()
										.getAvatar().length);
						fGame.whitePlayerImageView.setImageBitmap(bmp);
					} else {
						fGame.whitePlayerImageView
								.setImageDrawable(getResources().getDrawable(
										R.drawable.general_avatar_unknown));
					}

					if (game.getAccount().getConnection().getAvatar() != null) {
						Bitmap bmp = BitmapFactory
								.decodeByteArray(game.getAccount()
										.getConnection().getAvatar(), 0, game
										.getAccount().getConnection()
										.getAvatar().length);
						fGame.blackPlayerImageView.setImageBitmap(bmp);
					} else {
						fGame.blackPlayerImageView
								.setImageDrawable(getResources().getDrawable(
										R.drawable.general_avatar_unknown));
					}

				}

				Toast.makeText(ChessGameActivity.this, "Game started!",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void rematchRequestReceived() {

		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder db = new AlertDialog.Builder(
						ChessGameActivity.this);
				db.setTitle("Remtach?");
				String actions[] = new String[2];
				actions[0] = "Ok";
				actions[1] = "Cancel";
				db.setItems(actions, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							try {
								readyReceived();
								game.sendReady();
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						case 1:
							break;
						default:

							break;
						}
					}
				});

				AlertDialog ad = db.create();
				ad.setCancelable(true);
				ad.setCanceledOnTouchOutside(false);
				ad.show();

			}
		});
	}

	@Override
	public void resignReceived() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				ctrl.makeRemoteMove("resign");
			}
		});
	}
	
	 private final Dialog promoteDialog() {
	        final CharSequence[] items = {
	            getString(R.string.queen), getString(R.string.rook),
	            getString(R.string.bishop), getString(R.string.knight)
	        };
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(R.string.promote_pawn_to);
	        builder.setItems(items, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                ctrl.reportPromotePiece(item);
	            }
	        });
	        AlertDialog alert = builder.create();
	        return alert;
	    }

}
