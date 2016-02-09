//
//  ViewController.h
//  Multicast
//
//  Created by Rick on 2/6/16.
//  Copyright © 2016 Rick Kaseguma. All rights reserved.
//

#import <UIKit/UIKit.h>

// http://cocoadocs.org/docsets/CocoaAsyncSocket/7.4.1/
// https://github.com/robbiehanson/CocoaAsyncSocket
#import "GCDAsyncUdpSocket.h"


@interface ViewController : UIViewController <GCDAsyncUdpSocketDelegate, UITableViewDataSource, UITableViewDelegate> {
}

- (void)setupSocket;
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didReceiveData:(NSData *)data
      fromAddress:(NSData *)address withFilterContext:(id)filterContext;
- (NSInteger)tableView:(UITableView *)tableView
 numberOfRowsInSection:(NSInteger)section;
- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath;


@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSMutableArray *items;
@property (strong, nonatomic) GCDAsyncUdpSocket *udpSocket;

@end

